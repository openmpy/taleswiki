package com.openmpy.taleswiki.board.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class BoardTitleTest {

    @DisplayName("[통과] 게시글 제목 클래스를 생성한다.")
    @Test
    void board_title_test_01() {
        // given
        final String value = "제목";

        // when
        final BoardTitle title = new BoardTitle(value);

        // then
        assertThat(title.getValue()).isEqualTo("제목");
    }

    @DisplayName("[예외] 게시글 제목이 빈 값일 수 없다.")
    @ParameterizedTest(name = "값: {0}")
    @NullAndEmptySource
    void 예외_board_title_test_01(final String value) {
        // when & then
        assertThatThrownBy(() -> new BoardTitle(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("제목이 공백일 수 없습니다.");
    }
}