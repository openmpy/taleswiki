package com.openmpy.taleswiki.board.application;

import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.repository.BoardRepository;
import com.openmpy.taleswiki.board.dto.request.BoardSaveRequest;
import com.openmpy.taleswiki.board.dto.response.BoardGetResponse;
import com.openmpy.taleswiki.board.dto.response.BoardGetsResponse;
import com.openmpy.taleswiki.board.dto.response.BoardSaveResponse;
import com.openmpy.taleswiki.common.application.ImageS3Service;
import com.openmpy.taleswiki.common.application.RedisService;
import com.openmpy.taleswiki.common.dto.PaginatedResponse;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.common.properties.ImageProperties;
import com.openmpy.taleswiki.common.util.IpAddressUtil;
import com.openmpy.taleswiki.member.application.MemberService;
import com.openmpy.taleswiki.member.domain.entity.Member;
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
public class BoardService {

    private static final String IMAGE_URL_PATTERN = "(!\\[[^]]*]\\(%s/images)/([a-f0-9\\-]+\\.webp\\))";

    private final MemberService memberService;
    private final ImageS3Service imageS3Service;
    private final RedisService redisService;
    private final BoardRepository boardRepository;
    private final ImageProperties imageProperties;

    @Transactional
    public BoardSaveResponse save(
            final Long memberId, final HttpServletRequest servletRequest, final BoardSaveRequest request
    ) {
        final Member member = memberService.get(memberId);
        final String clientIp = IpAddressUtil.getClientIp(servletRequest);
        final String content = imageS3Service.processImageReferences(request.content());

        final Board board = Board.save(request.title(), content, "테붕이" + member.getId(), clientIp, member);
        final Board savedBoard = boardRepository.save(board);
        return new BoardSaveResponse(savedBoard.getId());
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<BoardGetsResponse> gets(final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        final Page<Board> boards = boardRepository.findAll(pageRequest);

        final String imageUrlRegex = String.format(IMAGE_URL_PATTERN, imageProperties.uploadPath());
        final Pattern imagePattern = Pattern.compile(imageUrlRegex);

        final Page<BoardGetsResponse> responses = boards.map(
                it -> new BoardGetsResponse(
                        it.getId(),
                        it.getTitle(),
                        it.getAuthor(),
                        it.getCreatedAt(),
                        it.getView(),
                        imagePattern.matcher(it.getContent()).find()
                ));

        return PaginatedResponse.of(responses);
    }

    @Transactional(readOnly = true)
    public BoardGetResponse get(final HttpServletRequest request, final Long boardId) {
        final Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException("찾을 수 없는 게시글 번호입니다."));

        final String clientIp = IpAddressUtil.getClientIp(request);
        final String key = String.format("board-view_%d:%s", boardId, clientIp);

        if (redisService.setIfAbsent(key, "true", Duration.ofHours(1L))) {
            final String viewKey = String.format("board-view:%d", boardId);
            redisService.increment(viewKey);
        }
        return BoardGetResponse.of(board);
    }

    @Transactional
    public void incrementViews(final Long boardId, final Long count) {
        final Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException("찾을 수 없는 게시글 번호입니다."));

        board.incrementViews(count);
    }
}
