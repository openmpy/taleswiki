package com.openmpy.taleswiki.board.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.member.domain.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardTest {

    @DisplayName("[통과] 게시글 클래스를 생성한다.")
    @Test
    void board_test_01() {
        // given
        final Member member = Fixture.createMember();

        // when
        final Board board = new Board("제목", "내용", "작성자", 1L, "127.0.0.1", member);

        // then
        assertThat(board.getTitle()).isEqualTo("제목");
        assertThat(board.getContent()).isEqualTo("내용");
        assertThat(board.getAuthor()).isEqualTo("작성자");
        assertThat(board.getView()).isEqualTo(1L);
        assertThat(board.getIp()).isEqualTo("127.0.0.1");
        assertThat(board.getMember()).isEqualTo(member);
    }

    @DisplayName("[통과] 게시글을 작성한다.")
    @Test
    void board_test_02() {
        // given
        final Member member = Fixture.createMember();

        // when
        final Board board = Board.save("제목", "내용", "작성자", "127.0.0.1", member);

        // then
        assertThat(board.getTitle()).isEqualTo("제목");
        assertThat(board.getContent()).isEqualTo("내용");
        assertThat(board.getAuthor()).isEqualTo("작성자");
        assertThat(board.getIp()).isEqualTo("127.0.0.1");
        assertThat(board.getMember()).isEqualTo(member);
    }

    @DisplayName("[통과] 게시글 조회수를 증가시킨다.")
    @Test
    void board_test_03() {
        // given
        final Board board = Fixture.createBoard();

        // when
        board.incrementViews(10L);

        // then
        assertThat(board.getView()).isEqualTo(10L);
    }

    @DisplayName("[통과] 게시글을 수정한다.")
    @Test
    void board_test_04() {
        // given
        final Board board = Fixture.createBoard();

        // when
        board.update("수정_제목", "수정_내용");

        // then
        assertThat(board.getTitle()).isEqualTo("수정_제목");
        assertThat(board.getContent()).isEqualTo("수정_내용");
    }

    @DisplayName("[통과] 게시글에 좋아요를 누른다.")
    @Test
    void board_test_05() {
        // given
        final Board board = Fixture.createBoard();
        final BoardLike boardLike = BoardLike.save(board, null);

        // when
        board.addLike(boardLike);

        // then
        assertThat(board.getLikes()).hasSize(1);
    }

    @DisplayName("[통과] 게시글에 싫어요를 누른다.")
    @Test
    void board_test_06() {
        // given
        final Board board = Fixture.createBoard();
        final BoardUnlike boardUnlike = BoardUnlike.save(board, null);

        // when
        board.addUnlike(boardUnlike);

        // then
        assertThat(board.getUnlikes()).hasSize(1);
    }

    @DisplayName("[통과] 게시글에 댓글을 작성한다.")
    @Test
    void board_test_07() {
        // given
        final Board board = Fixture.createBoard();
        final BoardComment comment = new BoardComment("작성자", "내용", "127.0.0.1", null, board, null);

        // when
        board.addComment(comment);

        // then
        assertThat(board.getComments()).hasSize(1);
    }
}