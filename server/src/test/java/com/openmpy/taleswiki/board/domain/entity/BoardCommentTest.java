package com.openmpy.taleswiki.board.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.member.domain.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardCommentTest {

    @DisplayName("[통과] 게시글 댓글 클래스를 생성한다.")
    @Test
    void board_comment_test_01() {
        // given
        final Member member = Fixture.createMember();
        final Board board = Fixture.createBoard();

        // when
        final BoardComment comment = new BoardComment("작성자", "내용", "127.0.0.1", member, board, null);

        // then
        assertThat(comment.getAuthor()).isEqualTo("작성자");
        assertThat(comment.getContent()).isEqualTo("내용");
        assertThat(comment.getIp()).isEqualTo("127.0.0.1");
        assertThat(comment.getMember()).isEqualTo(member);
        assertThat(comment.getBoard()).isEqualTo(board);
        assertThat(comment.getParent()).isNull();
        assertThat(comment.getDepth()).isZero();
    }

    @DisplayName("[통과] 게시글에 댓글을 작성한다.")
    @Test
    void board_comment_test_02() {
        // given
        final Member member = Fixture.createMember();
        final Board board = Fixture.createBoard();
        final BoardComment comment = new BoardComment("작성자", "내용", "127.0.0.1", member, board, null);

        // when
        final BoardComment saved = BoardComment.save("작성자2", "내용2", "127.0.0.2", member, board, comment);

        // then
        assertThat(saved.getAuthor()).isEqualTo("작성자2");
        assertThat(saved.getContent()).isEqualTo("내용2");
        assertThat(saved.getIp()).isEqualTo("127.0.0.2");
        assertThat(saved.getMember()).isEqualTo(member);
        assertThat(saved.getBoard()).isEqualTo(board);
        assertThat(saved.getParent()).isEqualTo(comment);
        assertThat(saved.getDepth()).isEqualTo(1);
    }

    @DisplayName("[통과] 댓글 내용을 수정한다.")
    @Test
    void board_comment_test_03() {
        // given
        final BoardComment comment = new BoardComment("작성자", "내용", "127.0.0.1", null, null, null);

        // when
        comment.update("수정_내용");

        // then
        assertThat(comment.getContent()).isEqualTo("수정_내용");
    }

    @DisplayName("[통과] 댓글을 삭제한다.")
    @Test
    void board_comment_test_04() {
        // given
        final BoardComment comment = new BoardComment("작성자", "내용", "127.0.0.1", null, null, null);

        // when
        comment.toggleDelete(Boolean.TRUE);

        // then
        assertThat(comment.getIsDeleted()).isTrue();
    }
}