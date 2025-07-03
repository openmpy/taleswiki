package com.openmpy.taleswiki.board.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardContentTest {

    @DisplayName("[통과] 게시글 내용 클래스를 생성한다.")
    @Test
    void board_content_test_01() {
        // given
        final String value = "내용";

        // when
        final BoardContent content = new BoardContent(value);

        // then
        assertThat(content.getValue()).isEqualTo("내용");
    }
}