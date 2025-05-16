package com.openmpy.taleswiki.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class FileLoaderUtilTest {

    private Path tempDirectory;

    @BeforeEach
    void setUp() throws IOException {
        tempDirectory = Files.createTempDirectory("webp-test");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.walk(tempDirectory)
                .map(Path::toFile)
                .forEach(File::delete);
    }

    @DisplayName("[통과] 파일에서 확장자명을 구한다.")
    @Test
    void file_loader_util_test_01() {
        // given
        final MockMultipartFile multipartFile = new MockMultipartFile(
                "test.txt", "test.txt", "text/plain", "test".getBytes()
        );

        // when
        final String extension = FileLoaderUtil.getExtension(multipartFile);

        // then
        assertThat(extension).isEqualTo("txt");
    }

    @DisplayName("[통과] 이미지 파일 확장자가 유효하다.")
    @Test
    void file_loader_util_test_02() {
        // given
        final String extension = "png";

        // when & then
        assertThatNoException().isThrownBy(() -> FileLoaderUtil.validateImageFileExtension(extension));
    }

    @DisplayName("[통과] 이미지 파일을 webp로 변환한다.")
    @Test
    void file_loader_util_test_03() throws IOException {
        // given
        final File dummyJpgFile = createDummyImageFile("test1.jpg");
        final File dummyGifFile = createDummyGifFile("test2.gif");

        final String dummyJpgFileName = dummyJpgFile.getName();
        final String dummyGifFileName = dummyGifFile.getName();

        // when
        final File convertWebpFromJpg = FileLoaderUtil.convertWebp(dummyJpgFileName, dummyJpgFile);
        final File convertWebpFromGif = FileLoaderUtil.convertWebp(dummyGifFileName, dummyGifFile);

        // then
        assertThat(convertWebpFromJpg).hasName("test1.webp");
        assertThat(convertWebpFromJpg.length()).isLessThan(dummyJpgFile.length());
        assertThat(convertWebpFromGif).hasName("test2.webp");
        assertThat(convertWebpFromGif.length()).isLessThan(dummyGifFile.length());
    }

    @DisplayName("[통과] 내용에서 이미지 파일명을 추출한다.")
    @Test
    void file_loader_util_test_04() {
        // given
        final String url = "http://localhost:8080";
        final String content = "테스트\n"
                + "![테스트1](http://localhost:8080/images/tmp/test1.webp)\n"
                + "![테스트2](http://localhost:8080/images/tmp/test2.webp)";

        // when
        final List<String> fileNames = FileLoaderUtil.extractImageFileNames(url, content);

        // then
        assertThat(fileNames).hasSize(2);
        assertThat(fileNames.get(0)).isEqualTo("test1.webp");
        assertThat(fileNames.get(1)).isEqualTo("test2.webp");
    }

    @DisplayName("[예외] 파일에서 확장자명을 구하지 못한다.")
    @Test
    void 예외_file_loader_util_test_01() {
        // given
        final MockMultipartFile multipartFile = new MockMultipartFile(
                "test", "test", "text/plain", "test".getBytes()
        );

        // when & then
        assertThatThrownBy(() -> FileLoaderUtil.getExtension(multipartFile))
                .isInstanceOf(CustomException.class)
                .hasMessage("파일에서 확장자명을 추출할 수 없습니다.");
    }

    @DisplayName("[예외] 이미지 파일 확장자가 아니다.")
    @Test
    void 예외_file_loader_util_test_02() {
        // given
        final String extension = "pdf";

        // when & then
        assertThatThrownBy(() -> FileLoaderUtil.validateImageFileExtension(extension))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미지 파일 확장자가 올바르지 않습니다.");
    }

    @DisplayName("[예외] 이미지 파일을 WebP로 변환할 수 없다.")
    @Test
    void 예외_file_loader_util_test_03() {
        // given
        final File file = new File("test.png");

        // when & then
        assertThatThrownBy(() -> FileLoaderUtil.convertWebp(file.getName(), file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("WebP 변환 중 오류가 발생했습니다.");
    }

    private File createDummyImageFile(final String name) throws IOException {
        final File file = new File(tempDirectory.toFile(), name);
        final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        image.setRGB(0, 0, 0xFFFFFF);

        try (final OutputStream os = new FileOutputStream(file)) {
            ImageIO.write(image, "jpg", os);
        }
        return file;
    }

    private File createDummyGifFile(final String name) throws IOException {
        final File file = new File(tempDirectory.toFile(), name);
        final BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

        image.setRGB(0, 0, 0xFF00FF);

        try (final OutputStream os = new FileOutputStream(file)) {
            ImageIO.write(image, "gif", os);
        }
        return file;
    }
}