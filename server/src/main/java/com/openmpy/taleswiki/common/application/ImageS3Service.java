package com.openmpy.taleswiki.common.application;

import com.openmpy.taleswiki.common.dto.ImageUploadResponse;
import com.openmpy.taleswiki.common.properties.ImageProperties;
import com.openmpy.taleswiki.common.util.FileLoaderUtil;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageS3Service {

    private static final String IMAGE_URL_PATTERN = "(!\\[[^]]*]\\(%s/images)/tmp/([a-f0-9\\-]+\\.webp\\))";
    private static final String IMAGE_TMP_DIR = System.getProperty("user.home") + "/taleswiki/images/tmp";
    private static final String R2_IMAGE_TMP_SOURCE = "taleswiki/images/tmp/";
    private static final String R2_IMAGE_TMP_DIR = "images/tmp/";
    private static final String R2_IMAGE_DIR = "images/";
    private static final String BUCKET_NAME = "taleswiki";

    private final S3Client s3Client;
    private final ImageProperties imageProperties;

    @PostConstruct
    public void init() {
        final File dir = new File(IMAGE_TMP_DIR);

        if (!dir.exists()) {
            final boolean isCreated = dir.mkdirs();

            if (isCreated) {
                log.info("이미지 임시 디렉토리를 생성했습니다: {}", IMAGE_TMP_DIR);
                return;
            }

            log.warn("이미지 임시 디렉토리 생성에 실패했습니다: {}", IMAGE_TMP_DIR);
        }
    }

    public ImageUploadResponse upload(final MultipartFile file) {
        final String extension = FileLoaderUtil.getExtension(file);
        FileLoaderUtil.validateImageFileExtension(extension);

        final String fileName = UUID.randomUUID() + "." + extension;

        try {
            final Path uploadPath = Paths.get(IMAGE_TMP_DIR);
            final Path filePath = uploadPath.resolve(fileName);

            // 파일 저장
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            final File originalFile = filePath.toFile();

            // webp 변환 처리
            final File resultFile;

            if (extension.equalsIgnoreCase("webp")) {
                resultFile = originalFile;
            } else {
                resultFile = FileLoaderUtil.convertWebp(fileName, originalFile);
                Files.deleteIfExists(filePath); // 원본 삭제
            }

            // S3 업로드 경로 및 파일 정보
            final String key = String.format("images/tmp/%s", resultFile.getName());
            final long contentLength = resultFile.length();

            final PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .contentType("image/webp")
                    .contentLength(contentLength)
                    .build();

            try (final InputStream inputStream = new FileInputStream(resultFile)) {
                s3Client.putObject(objectRequest, RequestBody.fromInputStream(inputStream, contentLength));
            }

            Files.deleteIfExists(resultFile.toPath());
            return new ImageUploadResponse(resultFile.getName());
        } catch (final IOException e) {
            throw new IllegalArgumentException("이미지 업로드 중 오류가 발생했습니다.", e);
        }
    }

    public void moveToBaseDirectory(final String fileName) {
        final CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .copySource(R2_IMAGE_TMP_SOURCE + fileName)
                .key(R2_IMAGE_DIR + fileName)
                .build();

        s3Client.copyObject(copyObjectRequest);

        final DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(R2_IMAGE_TMP_DIR + fileName)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    public String processImageReferences(final String content) {
        final List<String> fileNames = FileLoaderUtil.extractImageFileNames(imageProperties.uploadPath(), content);
        for (final String fileName : fileNames) {
            moveToBaseDirectory(fileName);
        }

        final String imageUrlRegex = String.format(IMAGE_URL_PATTERN, imageProperties.uploadPath());
        final Pattern pattern = Pattern.compile(imageUrlRegex);
        final Matcher matcher = pattern.matcher(content);
        return matcher.replaceAll("$1/$2");
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteS3TmpImages() {
        final ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .prefix(R2_IMAGE_TMP_DIR)
                .build();

        final ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
        final Instant now = Instant.now();

        for (final S3Object s3Object : listObjectsV2Response.contents()) {
            final String key = s3Object.key();
            final Instant lastModified = s3Object.lastModified();

            if (ChronoUnit.HOURS.between(lastModified, now) >= 6) {
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(key)
                        .build();

                s3Client.deleteObject(deleteRequest);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteLocalTmpImages() {
        final Path imageTmpPath = Paths.get(IMAGE_TMP_DIR);

        try (final Stream<Path> paths = Files.list(imageTmpPath)) {
            final Instant now = Instant.now();

            paths.filter(Files::isRegularFile).forEach(path -> {
                try {
                    final FileTime lastModifiedTime = Files.getLastModifiedTime(path);

                    if (ChronoUnit.HOURS.between(lastModifiedTime.toInstant(), now) > 1) {
                        Files.delete(path);
                    }
                } catch (final IOException e) {
                    log.warn("이미지 파일 삭제에 실패했습니다. {}", e.getMessage());
                }
            });
        } catch (final IOException e) {
            throw new IllegalArgumentException("이미지 임시 파일 삭제중 오류가 발생했습니다.", e);
        }
    }
}
