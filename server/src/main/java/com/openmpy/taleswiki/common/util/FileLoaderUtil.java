package com.openmpy.taleswiki.common.util;

import com.openmpy.taleswiki.common.exception.CustomException;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.AnimatedGif;
import com.sksamuel.scrimage.nio.AnimatedGifReader;
import com.sksamuel.scrimage.nio.ImageSource;
import com.sksamuel.scrimage.webp.Gif2WebpWriter;
import com.sksamuel.scrimage.webp.WebpWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.web.multipart.MultipartFile;

public class FileLoaderUtil {

    private static final Set<String> VALID_FILE_EXTENSIONS = Set.of(
            "jpg", "jpeg", "png", "gif", "bmp", "webp", "tiff", "svg"
    );
    private static final String IMAGE_UPLOAD_PATTERN = "!\\[.*?]\\(%s/(images/tmp/\\S+)\\)";

    public static String getExtension(final MultipartFile file) {
        final String filename = file.getOriginalFilename();

        if (filename == null || !filename.contains(".")) {
            throw new CustomException("파일에서 확장자명을 추출할 수 없습니다.");
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    public static void validateFileExtension(final String extension) {
        if (!VALID_FILE_EXTENSIONS.contains(extension)) {
            throw new CustomException("이미지 파일 확장자가 올바르지 않습니다.");
        }
    }

    public static File convertWebp(final String fileName, final File file) {
        try {
            final String webpFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".webp";
            final String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            final File outputWebpFile = new File(file.getParentFile(), webpFileName);

            if (extension.equalsIgnoreCase("gif")) {
                final AnimatedGif animatedGif = AnimatedGifReader.read(ImageSource.of(file));
                return animatedGif.output(Gif2WebpWriter.DEFAULT, outputWebpFile);
            }

            return ImmutableImage.loader()
                    .fromFile(file)
                    .output(WebpWriter.DEFAULT, outputWebpFile);
        } catch (final IOException e) {
            throw new IllegalArgumentException("WebP 변환 중 오류가 발생했습니다.", e);
        }
    }

    public static List<String> extractImageFileNames(final String baseUrl, final String content) {
        final List<String> imagePaths = new ArrayList<>();

        final String imageFileRegex = String.format(IMAGE_UPLOAD_PATTERN, baseUrl);
        final Pattern pattern = Pattern.compile(imageFileRegex);
        final Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            final String imagePath = matcher.group(1);
            imagePaths.add(Paths.get(imagePath).getFileName().toString());
        }
        return imagePaths;
    }
}
