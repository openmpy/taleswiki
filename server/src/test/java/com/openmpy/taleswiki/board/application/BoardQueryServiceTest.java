package com.openmpy.taleswiki.board.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.repository.BoardRepository;
import com.openmpy.taleswiki.board.dto.response.BoardGetResponse;
import com.openmpy.taleswiki.board.dto.response.BoardGetsResponse;
import com.openmpy.taleswiki.common.dto.PaginatedResponse;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.helper.ServiceTestSupport;
import com.openmpy.taleswiki.member.domain.entity.Member;
import com.openmpy.taleswiki.member.domain.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BoardQueryServiceTest extends ServiceTestSupport {

    @Autowired
    private BoardQueryService boardQueryService;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("[통과] 이미지가 없는 게시글을 조회한다.")
    @Test
    void board_query_service_test_01() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member));

        // when
        final PaginatedResponse<BoardGetsResponse> response = boardQueryService.gets(0, 10);

        // then
        final BoardGetsResponse first = response.content().getFirst();

        assertThat(first.title()).isEqualTo("제목");
        assertThat(first.hasImage()).isFalse();
        assertThat(first.likes()).isZero();
        assertThat(first.commentsCount()).isZero();
        assertThat(first.nickname()).isEqualTo("테붕이01");
    }

    @DisplayName("[통과] 이미지가 있는 게시글을 조회한다.")
    @Test
    void board_query_service_test_02() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        final String content = "![](https://r2.taleswiki.com/images/15b6a88d-4606-4015-a6d6-fcb2c8db1696.webp)";
        boardRepository.save(Board.save("제목", content, "테붕이01", "127.0.0.1", member));

        // when
        final PaginatedResponse<BoardGetsResponse> response = boardQueryService.gets(0, 10);

        // then
        final BoardGetsResponse first = response.content().getFirst();

        assertThat(first.title()).isEqualTo("제목");
        assertThat(first.hasImage()).isTrue();
        assertThat(first.likes()).isZero();
        assertThat(first.commentsCount()).isZero();
        assertThat(first.nickname()).isEqualTo("테붕이01");
    }

    @DisplayName("[통과] 게시글을 조회한다.")
    @Test
    void board_query_service_test_03() {
        // given
        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();

        final Member member = memberRepository.save(Fixture.createMember());
        final Board board = boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member));

        // when
        final BoardGetResponse response = boardQueryService.get(servletRequest, board.getId());

        // then
        assertThat(response.title()).isEqualTo("제목");
        assertThat(response.content()).isEqualTo("내용");
        assertThat(response.author()).isEqualTo("테붕이01");
        assertThat(response.likes()).isZero();
        assertThat(response.like()).isZero();
        assertThat(response.unlike()).isZero();
        assertThat(response.comments()).isEmpty();

        final String key = String.format("board-view_%d:%s", board.getId(), "127.0.0.1");
        assertThat(redisTemplate.hasKey(key)).isTrue();

        final String viewKey = String.format("board-view:%d", board.getId());
        assertThat(redisTemplate.opsForValue().get(viewKey)).isEqualTo(1);
    }

    @DisplayName("[예외] 게시글을 찾을 수 없다.")
    @Test
    void 예외_board_query_service_test_01() {
        // when & then
        assertThatThrownBy(() -> boardQueryService.getBoard(999L))
                .isInstanceOf(CustomException.class)
                .hasMessage("찾을 수 없는 게시글 번호입니다.");
    }

    @DisplayName("[예외] 댓글을 찾을 수 없다.")
    @Test
    void 예외_board_query_service_test_02() {
        // when & then
        assertThatThrownBy(() -> boardQueryService.getComment(999L))
                .isInstanceOf(CustomException.class)
                .hasMessage("찾을 수 없는 댓글 번호입니다.");
    }
}