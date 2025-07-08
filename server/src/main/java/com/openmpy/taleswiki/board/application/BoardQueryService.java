package com.openmpy.taleswiki.board.application;

import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.entity.BoardComment;
import com.openmpy.taleswiki.board.domain.repository.BoardCommentRepository;
import com.openmpy.taleswiki.board.domain.repository.BoardRepository;
import com.openmpy.taleswiki.board.dto.response.BoardGetResponse;
import com.openmpy.taleswiki.board.dto.response.BoardGetsResponse;
import com.openmpy.taleswiki.common.application.RedisService;
import com.openmpy.taleswiki.common.dto.PaginatedResponse;
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
public class BoardQueryService {

    private final RedisService redisService;
    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    public PaginatedResponse<BoardGetsResponse> gets(final int page, final int size) {
        return boardRepository.gets(page, size);
    }

    @Transactional(readOnly = true)
    public BoardGetResponse get(final HttpServletRequest request, final Long boardId) {
        final BoardGetResponse response = boardRepository.get(boardId);
        preventDuplicateViewAndCount(request, boardId);
        return response;
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<BoardGetsResponse> getsOfMember(final Long memberId, final int page, final int size) {
        final Member member = memberService.getMember(memberId);
        return boardRepository.getsOfMember(member, page, size);
    }

    public Board getBoard(final Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException("찾을 수 없는 게시글 번호입니다."));
    }

    public BoardComment getComment(final Long commentId) {
        return boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("찾을 수 없는 댓글 번호입니다."));
    }

    private void preventDuplicateViewAndCount(final HttpServletRequest request, final Long boardId) {
        final String clientIp = IpAddressUtil.getClientIp(request);
        final String key = String.format("board-view_%d:%s", boardId, clientIp);

        if (redisService.setIfAbsent(key, "true", Duration.ofHours(1L))) {
            final String viewKey = String.format("board-view:%d", boardId);
            redisService.increment(viewKey);
        }
    }
}
