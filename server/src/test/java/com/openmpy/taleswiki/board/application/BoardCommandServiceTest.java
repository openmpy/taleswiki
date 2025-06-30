package com.openmpy.taleswiki.board.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.repository.BoardRepository;
import com.openmpy.taleswiki.board.dto.request.BoardSaveRequest;
import com.openmpy.taleswiki.board.dto.response.BoardSaveResponse;
import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.helper.ServiceTestSupport;
import com.openmpy.taleswiki.member.domain.entity.Member;
import com.openmpy.taleswiki.member.domain.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BoardCommandServiceTest extends ServiceTestSupport {

    @Autowired
    private BoardCommandService boardCommandService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    @DisplayName("[통과] 게시글을 작성한다.")
    @Test
    void board_command_service_test_01() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final BoardSaveRequest request = new BoardSaveRequest("제목", "내용");

        // stub
        when(imageS3Service.processImageReferences(anyString())).thenReturn("내용");

        // when
        final BoardSaveResponse response = boardCommandService.save(member.getId(), servletRequest, request);

        // then
        assertThat(response.boardId()).isNotNull();
    }

    @DisplayName("[통과] 게시글 조회수를 증가시킨다.")
    @Test
    void board_command_service_test_02() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        final Board board = boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member));

        // when
        boardCommandService.incrementViews(board.getId(), 1L);

        // then
        final Board first = boardRepository.findAll().getFirst();
        assertThat(first.getView()).isEqualTo(1L);
    }

    @DisplayName("[통과] 게시글을 삭제한다.")
    @Test
    void board_command_service_test_03() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        final Board board = boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member));

        // when
        boardCommandService.delete(member.getId(), board.getId());

        // then
        final long count = boardRepository.count();
        assertThat(count).isZero();
    }
}