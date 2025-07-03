package com.openmpy.taleswiki.board.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.member.domain.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardLikeTest {

    @DisplayName("[통과] 게시글 좋아요 클래스를 생성한다.")
    @Test
    void board_like_test_01() {
        // given
        final Board board = Fixture.createBoard();
        final Member member = Fixture.createMember();

        // when
        final BoardLike boardLike = new BoardLike(board, member);

        // then
        assertThat(boardLike.getBoard()).isEqualTo(board);
        assertThat(boardLike.getMember()).isEqualTo(member);
    }

    @DisplayName("[통과] 게시글 좋아요를 누른다.")
    @Test
    void board_like_test_02() {
        // given
        final Board board = Fixture.createBoard();
        final Member member = Fixture.createMember();

        // when
        final BoardLike boardLike = BoardLike.save(board, member);

        // then
        assertThat(boardLike.getBoard()).isEqualTo(board);
        assertThat(boardLike.getMember()).isEqualTo(member);
    }
}