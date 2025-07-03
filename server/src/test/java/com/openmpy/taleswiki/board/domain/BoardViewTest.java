package com.openmpy.taleswiki.board.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardViewTest {

    @DisplayName("[통과] 게시글 조회수 클래스를 생성한다.")
    @Test
    void board_view_test_01() {
        // given
        final long value = 1L;

        // when
        final BoardView boardView = new BoardView(value);

        // then
        assertThat(boardView.getValue()).isEqualTo(1L);
    }

    @DisplayName("[통과] 게시글 조회수를 증가시킨다.")
    @Test
    void board_view_test_02() {
        // given
        final long value = 1L;
        final BoardView boardView = new BoardView(value);

        // when
        boardView.increment(10L);

        // then
        assertThat(boardView.getValue()).isEqualTo(11L);
    }

    @DisplayName("[예외] 게시글 조회수가 음수일 수 없다.")
    @Test
    void 예외_board_view_test_01() {
        // given
        final long value = -10L;

        // when & then
        assertThatThrownBy(() -> new BoardView(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("조회수가 음수일 수 없습니다.");
    }
}