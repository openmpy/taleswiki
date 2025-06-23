package com.openmpy.taleswiki.board.application;

import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.entity.BoardLike;
import com.openmpy.taleswiki.board.domain.entity.BoardUnlike;
import com.openmpy.taleswiki.board.domain.repository.BoardLikeRepository;
import com.openmpy.taleswiki.board.domain.repository.BoardRepository;
import com.openmpy.taleswiki.board.domain.repository.BoardUnlikeRepository;
import com.openmpy.taleswiki.board.dto.request.BoardSaveRequest;
import com.openmpy.taleswiki.board.dto.request.BoardUpdateRequest;
import com.openmpy.taleswiki.board.dto.response.BoardGetResponse;
import com.openmpy.taleswiki.board.dto.response.BoardGetsResponse;
import com.openmpy.taleswiki.board.dto.response.BoardSaveResponse;
import com.openmpy.taleswiki.board.dto.response.BoardUpdateResponse;
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
    private final BoardLikeRepository boardLikeRepository;
    private final BoardUnlikeRepository boardUnlikeRepository;
    private final ImageProperties imageProperties;

    @Transactional
    public BoardSaveResponse save(
            final Long memberId, final HttpServletRequest servletRequest, final BoardSaveRequest request
    ) {
        final Member member = memberService.get(memberId);
        final String clientIp = IpAddressUtil.getClientIp(servletRequest);
        final String content = imageS3Service.processImageReferences(request.content());

        final Board board = Board.save(request.title(), content, "테붕이" + member.getId(), clientIp, member);

        validateBoardSubmission(clientIp);

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
        final Board board = getBoard(boardId);
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
        final Board board = getBoard(boardId);
        board.incrementViews(count);
    }

    @Transactional
    public void delete(final Long memberId, final Long boardId) {
        final Member member = memberService.get(memberId);
        final Board board = getBoard(boardId);

        if (!board.getMember().equals(member)) {
            throw new CustomException("게시글 작성자가 일치하지 않습니다.");
        }

        boardRepository.delete(board);
    }

    @Transactional
    public BoardUpdateResponse update(final Long memberId, final Long boardId, final BoardUpdateRequest request) {
        final Member member = memberService.get(memberId);
        final Board board = getBoard(boardId);

        if (!board.getMember().equals(member)) {
            throw new CustomException("게시글 작성자가 일치하지 않습니다.");
        }

        final String content = imageS3Service.processImageReferences(request.content());
        board.update(request.title(), content);

        return new BoardUpdateResponse(board.getId());
    }

    @Transactional
    public void like(final Long memberId, final Long boardId) {
        final Member member = memberService.get(memberId);
        final Board board = getBoard(boardId);

        if (boardLikeRepository.existsByBoardAndMember(board, member)) {
            throw new CustomException("이미 좋아요를 누른 게시글입니다.");
        }

        final BoardLike boardLike = BoardLike.save(board, member);
        board.addLike(boardLike);
    }

    @Transactional
    public void unlike(final Long memberId, final Long boardId) {
        final Member member = memberService.get(memberId);
        final Board board = getBoard(boardId);

        if (boardUnlikeRepository.existsByBoardAndMember(board, member)) {
            throw new CustomException("이미 싫어요를 누른 게시글입니다.");
        }

        final BoardUnlike boardUnlike = BoardUnlike.save(board, member);
        board.addUnlike(boardUnlike);
    }

    private void validateBoardSubmission(final String clientIp) {
        final String key = String.format("board-save:%s", clientIp);

        if (!redisService.setIfAbsent(key, "true", Duration.ofMinutes(1L))) {
            throw new CustomException("1분 후에 게시글을 작성할 수 있습니다.");
        }
    }

    private Board getBoard(final Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException("찾을 수 없는 게시글 번호입니다."));
    }
}
