package com.openmpy.taleswiki.board.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.member.domain.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardUnlikeTest {

    @DisplayName("[통과] 게시글 싫어요 클래스를 생성한다.")
    @Test
    void board_unlike_test_01() {
        // given
        final Board board = Fixture.createBoard();
        final Member member = Fixture.createMember();

        // when
        final BoardUnlike boardUnlike = new BoardUnlike(board, member);

        // then
        assertThat(boardUnlike.getBoard()).isEqualTo(board);
        assertThat(boardUnlike.getMember()).isEqualTo(member);
    }

    @DisplayName("[통과] 게시글 싫어요를 누른다.")
    @Test
    void board_unlike_test_02() {
        // given
        final Board board = Fixture.createBoard();
        final Member member = Fixture.createMember();

        // when
        final BoardUnlike boardUnlike = BoardUnlike.save(board, member);

        // then
        assertThat(boardUnlike.getBoard()).isEqualTo(board);
        assertThat(boardUnlike.getMember()).isEqualTo(member);
    }
}