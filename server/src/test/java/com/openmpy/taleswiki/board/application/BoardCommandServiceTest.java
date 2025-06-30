package com.openmpy.taleswiki.board.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.entity.BoardComment;
import com.openmpy.taleswiki.board.domain.entity.BoardLike;
import com.openmpy.taleswiki.board.domain.entity.BoardUnlike;
import com.openmpy.taleswiki.board.domain.repository.BoardCommentRepository;
import com.openmpy.taleswiki.board.domain.repository.BoardLikeRepository;
import com.openmpy.taleswiki.board.domain.repository.BoardRepository;
import com.openmpy.taleswiki.board.domain.repository.BoardUnlikeRepository;
import com.openmpy.taleswiki.board.dto.request.BoardSaveRequest;
import com.openmpy.taleswiki.board.dto.request.BoardUpdateRequest;
import com.openmpy.taleswiki.board.dto.request.CommentSaveRequest;
import com.openmpy.taleswiki.board.dto.response.BoardSaveResponse;
import com.openmpy.taleswiki.board.dto.response.BoardUpdateResponse;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.helper.Fixture;
import com.openmpy.taleswiki.helper.ServiceTestSupport;
import com.openmpy.taleswiki.member.domain.constants.MemberSocial;
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

    @Autowired
    private BoardLikeRepository boardLikeRepository;

    @Autowired
    private BoardUnlikeRepository boardUnlikeRepository;

    @Autowired
    private BoardCommentRepository boardCommentRepository;

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

    @DisplayName("[통과] 게시글을 수정한다.")
    @Test
    void board_command_service_test_04() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        final Board board = boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member));

        final BoardUpdateRequest request = new BoardUpdateRequest("수정_제목", "수정_내용");

        // stub
        when(imageS3Service.processImageReferences(anyString())).thenReturn("수정_내용");

        // when
        final BoardUpdateResponse response = boardCommandService.update(member.getId(), board.getId(), request);

        // then
        assertThat(response.boardId()).isNotNull();
    }

    @DisplayName("[통과] 게시글에 좋아요를 누른다.")
    @Test
    void board_command_service_test_05() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        final Board board = boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member));

        // when
        boardCommandService.like(member.getId(), board.getId());

        // then
        final Board foundBoard = boardRepository.findById(board.getId()).get();
        assertThat(foundBoard.getLikes()).hasSize(1);
    }

    @DisplayName("[통과] 게시글에 싫어요를 누른다.")
    @Test
    void board_command_service_test_06() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        final Board board = boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member));

        // when
        boardCommandService.unlike(member.getId(), board.getId());

        // then
        final Board foundBoard = boardRepository.findById(board.getId()).get();
        assertThat(foundBoard.getUnlikes()).hasSize(1);
    }

    @DisplayName("[통과] 댓글을 작성한다.")
    @Test
    void board_command_service_test_07() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        final Board board = boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member));

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final CommentSaveRequest request = new CommentSaveRequest(null, "댓글_내용");

        // when
        boardCommandService.saveComment(member.getId(), board.getId(), servletRequest, request);

        // then
        final Board foundBoard = boardRepository.findById(board.getId()).get();
        assertThat(foundBoard.getComments()).hasSize(1);
    }

    @DisplayName("[통과] 대댓글을 작성한다.")
    @Test
    void board_command_service_test_08() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        final Board board = boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member));
        board.addComment(BoardComment.save("테붕이01", "내용", "127.0.0.1", member, board, null));

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final CommentSaveRequest request = new CommentSaveRequest(board.getComments().getFirst().getId(), "댓글_내용");

        // when
        boardCommandService.saveComment(member.getId(), board.getId(), servletRequest, request);

        // then
        final Board foundBoard = boardRepository.findById(board.getId()).get();
        assertThat(foundBoard.getComments()).hasSize(2);
    }

    @DisplayName("[예외] 작성자가 일치하지 않아 게시글을 삭제할 수 없다.")
    @Test
    void 예외_board_command_service_test_01() {
        // given
        final Member member1 = memberRepository.save(Fixture.createMember());
        final Member member2 = memberRepository.save(Member.create("test2@test.com", MemberSocial.KAKAO));
        final Board board = boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member1));

        // when & then
        assertThatThrownBy(() -> boardCommandService.delete(member2.getId(), board.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("게시글 작성자가 일치하지 않습니다.");
    }

    @DisplayName("[예외] 작성자가 일치하지 않아 게시글을 수정할 수 없다.")
    @Test
    void 예외_board_command_service_test_02() {
        // given
        final Member member1 = memberRepository.save(Fixture.createMember());
        final Member member2 = memberRepository.save(Member.create("test2@test.com", MemberSocial.KAKAO));
        final Board board = boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member1));
        final BoardUpdateRequest request = new BoardUpdateRequest("수정_제목", "수정_내용");

        // when & then
        assertThatThrownBy(() -> boardCommandService.update(member2.getId(), board.getId(), request))
                .isInstanceOf(CustomException.class)
                .hasMessage("게시글 작성자가 일치하지 않습니다.");
    }

    @DisplayName("[예외] 이미 좋아요를 누른 게시글이다.")
    @Test
    void 예외_board_command_service_test_03() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        final Board board = boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member));
        boardLikeRepository.save(BoardLike.save(board, member));

        // when & then
        assertThatThrownBy(() -> boardCommandService.like(member.getId(), board.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 좋아요를 누른 게시글입니다.");
    }

    @DisplayName("[예외] 이미 싫어요를 누른 게시글이다.")
    @Test
    void 예외_board_command_service_test_04() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        final Board board = boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member));
        boardUnlikeRepository.save(BoardUnlike.save(board, member));

        // when & then
        assertThatThrownBy(() -> boardCommandService.unlike(member.getId(), board.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 싫어요를 누른 게시글입니다.");
    }

    @DisplayName("[예외] 부모가 존재하지 않는 댓글에 대댓글을 작성한다.")
    @Test
    void 예외_board_command_service_test_05() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        final Board board = boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member));

        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final CommentSaveRequest request = new CommentSaveRequest(999L, "댓글_내용");

        // when & then
        assertThatThrownBy(
                () -> boardCommandService.saveComment(member.getId(), board.getId(), servletRequest, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("존재하지 않는 부모 댓글입니다.");
    }

    @DisplayName("[예외] 삭제된 댓글에 대댓글을 작성한다.")
    @Test
    void 예외_board_command_service_test_06() {
        // given
        final Member member = memberRepository.save(Fixture.createMember());
        final Board board = boardRepository.save(Board.save("제목", "내용", "테붕이01", "127.0.0.1", member));
        board.addComment(BoardComment.save("테붕이01", "내용", "127.0.0.1", member, board, null));

        final BoardComment first = board.getComments().getFirst();
        first.toggleDelete(Boolean.TRUE);

        final BoardComment comment = boardCommentRepository.findAll().getFirst();
        final HttpServletRequest servletRequest = Fixture.createMockHttpServletRequest();
        final CommentSaveRequest request = new CommentSaveRequest(comment.getId(), "댓글_내용");

        // when & then
        assertThatThrownBy(
                () -> boardCommandService.saveComment(member.getId(), board.getId(), servletRequest, request))
                .isInstanceOf(CustomException.class)
                .hasMessage("삭제된 댓글에 대댓글을 작성할 수 없습니다.");
    }
}