package com.openmpy.taleswiki.board.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.entity.BoardUnlike;
import com.openmpy.taleswiki.board.domain.repository.BoardRepository;
import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.helper.ServiceTestSupport;
import com.openmpy.taleswiki.member.domain.constants.MemberSocial;
import com.openmpy.taleswiki.member.domain.entity.Member;
import com.openmpy.taleswiki.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BoardSchedulerTest extends ServiceTestSupport {

    @Autowired
    private BoardScheduler boardScheduler;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @DisplayName("[통과] 게시글 조회수를 DB에 동기화한다.")
    @Test
    void board_scheduler_test_01() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        final Board board = boardRepository.save(Board.save("제목", "내용", "작성자", "127.0.0.1", member));

        final String key = String.format("board-view:%d", board.getId());
        redisTemplate.opsForValue().increment(key, 10L);

        // when
        boardScheduler.syncViewCountsToDataBase();

        // then
        final Board foundBoard = boardRepository.findById(board.getId()).get();
        assertThat(foundBoard.getView()).isEqualTo(10L);
    }

    @DisplayName("[통과] 게시글에 싫어요가 10개 이상일 시 삭제된다.")
    @Test
    void board_scheduler_test_02() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        final Board board = boardRepository.save(Board.save("제목", "내용", "작성자", "127.0.0.1", member));

        for (int i = 0; i < 10; i++) {
            final String email = "test" + i + "@test.com";
            final Member customMember = memberRepository.save(Member.create(email, "테붕이", MemberSocial.GOOGLE));
            final BoardUnlike unlike = BoardUnlike.save(board, customMember);

            board.addUnlike(unlike);
        }

        // when
        boardScheduler.deleteUnlikeBoard();

        // then
        final long count = boardRepository.count();
        assertThat(count).isZero();
    }
}