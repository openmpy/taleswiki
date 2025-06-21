package com.openmpy.taleswiki.board.application;

import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.repository.BoardRepository;
import com.openmpy.taleswiki.board.dto.request.BoardSaveRequest;
import com.openmpy.taleswiki.board.dto.response.BoardSaveResponse;
import com.openmpy.taleswiki.common.util.IpAddressUtil;
import com.openmpy.taleswiki.member.application.MemberService;
import com.openmpy.taleswiki.member.domain.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final MemberService memberService;
    private final BoardRepository boardRepository;

    @Transactional
    public BoardSaveResponse save(
            final Long memberId, final HttpServletRequest servletRequest, final BoardSaveRequest request
    ) {
        final Member member = memberService.get(memberId);
        final String clientIp = IpAddressUtil.getClientIp(servletRequest);
        final Board board = Board.save(request.title(), request.content(), clientIp, member);
        final Board savedBoard = boardRepository.save(board);
        return new BoardSaveResponse(savedBoard.getId());
    }
}
