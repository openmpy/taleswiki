package com.openmpy.taleswiki.board.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class BoardAuthorTest {

    @DisplayName("[통과] 게시글 작성자 클래스를 생성한다.")
    @Test
    void board_author_test_01() {
        // given
        final String value = "작성자";

        // when
        final BoardAuthor author = new BoardAuthor(value);

        // then
        assertThat(author.getValue()).isEqualTo("작성자");
    }

    @DisplayName("[예외] 게시글 작성자 명이 공백일 수 없다.")
    @ParameterizedTest(name = "값: {0}")
    @NullAndEmptySource
    void 예외_board_author_test_01(final String value) {
        // when & then
        assertThatThrownBy(() -> new BoardAuthor(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("작성자가 공백일 수 없습니다.");
    }
}