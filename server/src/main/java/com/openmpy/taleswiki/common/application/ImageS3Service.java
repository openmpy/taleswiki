package com.openmpy.taleswiki.common.application;

import com.openmpy.taleswiki.common.dto.ImageUploadResponse;
import com.openmpy.taleswiki.common.util.FileLoaderUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@Service
public class ImageS3Service {

    private static final String IMAGE_TMP_DIR = System.getProperty("user.home") + "/taleswiki/images/tmp";
    private static final String R2_IMAGE_TMP_SOURCE = "taleswiki/images/tmp/";
    private static final String R2_IMAGE_TMP_DIR = "images/tmp/";
    private static final String R2_IMAGE_DIR = "images/";
    private static final String BUCKET_NAME = "taleswiki";

    private final S3Client s3Client;

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

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteTmpImages() {
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
}
