package com.openmpy.taleswiki.common.application;

import com.openmpy.taleswiki.common.dto.ImageUploadResponse;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.common.util.FileLoaderUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {

    private static final String IMAGE_TMP_DIR = System.getProperty("user.home") + "/taleswiki/images/tmp";
    private static final String IMAGE_BASE_DIR = System.getProperty("user.home") + "/taleswiki/images/base";
    private static final long IMAGE_DELETE_HOURS = 6;

    private final Environment environment;

    @PostConstruct
    public void initDirectory() {
        final File tmpImageDir = new File(IMAGE_TMP_DIR);
        final File baseImageDir = new File(IMAGE_BASE_DIR);

        if (!tmpImageDir.exists() && !tmpImageDir.mkdirs()) {
            throw new CustomException("이미지 임시 폴더 생성에 실패했습니다.");
        }
        if (!baseImageDir.exists() && !baseImageDir.mkdirs()) {
            throw new CustomException("이미지 기본 폴더 생성에 실패했습니다.");
        }
    }

    @PreDestroy
    public void destroyDirectory() {
        final File tmpImageDir = new File(IMAGE_TMP_DIR);
        final File baseImageDir = new File(IMAGE_BASE_DIR);

        cleanDirectory(tmpImageDir);
        cleanDirectory(baseImageDir);
    }

    public ImageUploadResponse upload(final MultipartFile file) {
        final String extension = FileLoaderUtil.getExtension(file);
        FileLoaderUtil.validateFileExtension(extension);

        try {
            final String fileName = UUID.randomUUID() + "." + extension;
            final Path uploadPath = Paths.get(IMAGE_TMP_DIR);
            final Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            final File originalFile = filePath.toFile();
            final File resultFile;

            if (extension.equalsIgnoreCase("webp")) {
                resultFile = originalFile;
            } else {
                resultFile = FileLoaderUtil.convertWebp(fileName, originalFile);
                Files.delete(filePath);
            }
            return new ImageUploadResponse(resultFile.getName());
        } catch (final IOException e) {
            throw new IllegalArgumentException("이미지 업로드중 오류가 발생했습니다.", e);
        }
    }

    public void moveToBaseDirectory(final String fileName) {
        final Path imageTmpPath = Paths.get(IMAGE_TMP_DIR + "/" + fileName);
        final Path imageBasePath = Paths.get(IMAGE_BASE_DIR + "/" + fileName);

        try {
            Files.move(imageTmpPath, imageBasePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new IllegalArgumentException("이미지 파일 이동중 오류가 발생했습니다.", e);
        }
    }

    public Resource getImageTmpResource(final String fileName) {
        return new FileSystemResource(IMAGE_TMP_DIR + "/" + fileName);
    }

    public Resource getImageResource(final String fileName) {
        return new FileSystemResource(IMAGE_BASE_DIR + "/" + fileName);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteTmpImages() {
        final Path imageTmpPath = Paths.get(IMAGE_TMP_DIR);

        try (final Stream<Path> paths = Files.list(imageTmpPath)) {
            final Instant now = Instant.now();

            paths.filter(Files::isRegularFile).forEach(path -> {
                try {
                    final FileTime lastModifiedTime = Files.getLastModifiedTime(path);
                    final long minutesBetween = ChronoUnit.HOURS.between(lastModifiedTime.toInstant(), now);

                    if (minutesBetween > IMAGE_DELETE_HOURS) {
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

    private void cleanDirectory(final File directory) {
        final File[] files = directory.listFiles();

        if (files != null) {
            for (final File file : files) {
                if (!file.delete()) {
                    log.error("파일 삭제에 실패했습니다. {}", file.getAbsolutePath());
                }
            }
        }
    }
}
