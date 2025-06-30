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
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BoardQueryService {

    private static final String IMAGE_URL_PATTERN = "!\\[[^]]*]\\(https?://[^)]+\\.(webp|png|jpg|jpeg|gif|bmp|svg)\\)";

    private final RedisService redisService;
    private final BoardRepository boardRepository;
    private final BoardCommentRepository boardCommentRepository;

    @Transactional(readOnly = true)
    public PaginatedResponse<BoardGetsResponse> gets(final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        final Page<Board> boards = boardRepository.findAll(pageRequest);

        final Pattern imagePattern = Pattern.compile(IMAGE_URL_PATTERN);
        final Page<BoardGetsResponse> responses = boards.map(
                it -> new BoardGetsResponse(
                        it.getId(),
                        it.getTitle(),
                        it.getAuthor(),
                        it.getCreatedAt(),
                        it.getView(),
                        it.getLikes().size() - it.getUnlikes().size(),
                        imagePattern.matcher(it.getContent()).find(),
                        it.getComments().size()
                ));

        return PaginatedResponse.of(responses);
    }

    @Transactional(readOnly = true)
    public BoardGetResponse get(final HttpServletRequest request, final Long boardId) {
        final Board board = getBoard(boardId);
        final String clientIp = IpAddressUtil.getClientIp(request);
        final String key = String.format("board-view_%d:%s", boardId, clientIp);

        if (redisService.setIfAbsent(key, "true", Duration.ofHours(1L))) {
            final String viewKey = String.format("board-view:%d", boardId);
            redisService.increment(viewKey);
        }
        return BoardGetResponse.of(board);
    }

    public Board getBoard(final Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException("찾을 수 없는 게시글 번호입니다."));
    }

    public BoardComment getComment(final Long commentId) {
        return boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("찾을 수 없는 댓글 번호입니다."));
    }
}
