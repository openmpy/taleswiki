package com.openmpy.taleswiki.board.application;

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
import com.openmpy.taleswiki.board.dto.request.CommentUpdateRequest;
import com.openmpy.taleswiki.board.dto.response.BoardSaveResponse;
import com.openmpy.taleswiki.board.dto.response.BoardUpdateResponse;
import com.openmpy.taleswiki.common.application.ImageS3Service;
import com.openmpy.taleswiki.common.application.RedisService;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.common.util.IpAddressUtil;
import com.openmpy.taleswiki.member.application.MemberService;
import com.openmpy.taleswiki.member.domain.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BoardCommandService {

    private final BoardQueryService boardQueryService;
    private final MemberService memberService;
    private final ImageS3Service imageS3Service;
    private final RedisService redisService;
    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final BoardUnlikeRepository boardUnlikeRepository;

    @Transactional
    public BoardSaveResponse save(
            final Long memberId, final HttpServletRequest servletRequest, final BoardSaveRequest request
    ) {
        final Member member = memberService.getMember(memberId);
        final String clientIp = IpAddressUtil.getClientIp(servletRequest);
        final String content = imageS3Service.processImageReferences(request.content());

        final Board board = Board.save(request.title(), content, member.getNickname(), clientIp, member);

        validateBoardSubmission(clientIp);

        final Board savedBoard = boardRepository.save(board);
        return new BoardSaveResponse(savedBoard.getId());
    }

    @Transactional
    public void incrementViews(final Long boardId, final Long count) {
        final Board board = boardQueryService.getBoard(boardId);
        board.incrementViews(count);
    }

    @Transactional
    public void delete(final Long memberId, final Long boardId) {
        final Member member = memberService.getMember(memberId);
        final Board board = boardQueryService.getBoard(boardId);

        if (!board.getMember().equals(member)) {
            throw new CustomException("게시글 작성자가 일치하지 않습니다.");
        }

        boardRepository.delete(board);
    }

    @Transactional
    public BoardUpdateResponse update(final Long memberId, final Long boardId, final BoardUpdateRequest request) {
        final Member member = memberService.getMember(memberId);
        final Board board = boardQueryService.getBoard(boardId);

        if (!board.getMember().equals(member)) {
            throw new CustomException("게시글 작성자가 일치하지 않습니다.");
        }

        final String content = imageS3Service.processImageReferences(request.content());
        board.update(request.title(), content);

        return new BoardUpdateResponse(board.getId());
    }

    @Transactional
    public void like(final Long memberId, final Long boardId) {
        final Member member = memberService.getMember(memberId);
        final Board board = boardQueryService.getBoard(boardId);

        if (boardLikeRepository.existsByBoardAndMember(board, member)) {
            throw new CustomException("이미 좋아요를 누른 게시글입니다.");
        }

        final BoardLike boardLike = BoardLike.save(board, member);
        board.addLike(boardLike);
    }

    @Transactional
    public void unlike(final Long memberId, final Long boardId) {
        final Member member = memberService.getMember(memberId);
        final Board board = boardQueryService.getBoard(boardId);

        if (boardUnlikeRepository.existsByBoardAndMember(board, member)) {
            throw new CustomException("이미 싫어요를 누른 게시글입니다.");
        }

        final BoardUnlike boardUnlike = BoardUnlike.save(board, member);
        board.addUnlike(boardUnlike);
    }

    @Transactional
    public void saveComment(
            final Long memberId,
            final Long boardId,
            final HttpServletRequest servletRequest,
            final CommentSaveRequest request
    ) {
        final Member member = memberService.getMember(memberId);
        final Board board = boardQueryService.getBoard(boardId);

        final String clientIp = IpAddressUtil.getClientIp(servletRequest);
        BoardComment parent = null;

        if (request.parentId() != null) {
            parent = boardCommentRepository.findById(request.parentId())
                    .orElseThrow(() -> new CustomException("존재하지 않는 부모 댓글입니다."));

            if (parent.getIsDeleted() == Boolean.TRUE) {
                throw new CustomException("삭제된 댓글에 대댓글을 작성할 수 없습니다.");
            }
        }

        final BoardComment comment = BoardComment.save(
                member.getNickname(), request.content(), clientIp, member, board, parent
        );

        board.addComment(comment);
    }

    @Transactional
    public void updateComment(final Long memberId, final Long commentId, final CommentUpdateRequest request) {
        final Member member = memberService.getMember(memberId);
        final BoardComment comment = boardQueryService.getComment(commentId);

        if (!member.equals(comment.getMember())) {
            throw new CustomException("댓글 작성자가 일치하지 않습니다.");
        }
        if (comment.getIsDeleted() == Boolean.TRUE) {
            throw new CustomException("삭제된 댓글은 수정할 수 없습니다.");
        }

        comment.update(request.content());
    }

    @Transactional
    public void deleteComment(final Long memberId, final Long commentId) {
        final Member member = memberService.getMember(memberId);
        final BoardComment comment = boardQueryService.getComment(commentId);

        if (!member.equals(comment.getMember())) {
            throw new CustomException("댓글 작성자가 일치하지 않습니다.");
        }
        if (comment.getIsDeleted() == Boolean.TRUE) {
            throw new CustomException("이미 삭제된 댓글입니다.");
        }

        comment.toggleDelete(Boolean.TRUE);
    }

    private void validateBoardSubmission(final String clientIp) {
        final String key = String.format("board-save:%s", clientIp);

        if (!redisService.setIfAbsent(key, "true", Duration.ofMinutes(1L))) {
            throw new CustomException("1분 후에 게시글을 작성할 수 있습니다.");
        }
    }
}
