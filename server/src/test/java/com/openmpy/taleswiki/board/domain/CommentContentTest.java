package com.openmpy.taleswiki.board.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentContentTest {

    @DisplayName("[통과] 댓글 내용 클래스를 생성한다.")
    @Test
    void comment_content_test_01() {
        // given
        final String value = "내용";

        // when
        final CommentContent commentContent = new CommentContent(value);

        // then
        assertThat(commentContent.getValue()).isEqualTo("내용");
    }
}