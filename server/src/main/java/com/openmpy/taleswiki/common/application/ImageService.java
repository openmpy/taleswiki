package com.openmpy.taleswiki.common.application;

import com.openmpy.taleswiki.common.dto.ImageUploadResponse;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.common.util.FileLoaderUtil;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {

    private static final String IMAGE_TMP_DIR = System.getProperty("user.home") + "/taleswiki/images/tmp";
    private static final String IMAGE_BASE_DIR = System.getProperty("user.home") + "/taleswiki/images/base";

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
}
