package com.openmpy.taleswiki.board.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.repository.BoardRepository;
import com.openmpy.taleswiki.board.dto.response.BoardGetsResponse;
import com.openmpy.taleswiki.common.dto.PaginatedResponse;
import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.helper.ServiceTestSupport;
import com.openmpy.taleswiki.member.domain.entity.Member;
import com.openmpy.taleswiki.member.domain.repository.MemberRepository;
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
}