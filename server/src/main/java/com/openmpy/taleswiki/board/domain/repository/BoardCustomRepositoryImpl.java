package com.openmpy.taleswiki.board.domain.repository;

import com.openmpy.taleswiki.admin.dto.response.AdminGetBoardsResponse;
import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.entity.QBoard;
import com.openmpy.taleswiki.board.domain.entity.QBoardComment;
import com.openmpy.taleswiki.board.domain.entity.QBoardLike;
import com.openmpy.taleswiki.board.domain.entity.QBoardUnlike;
import com.openmpy.taleswiki.board.dto.response.BoardGetResponse;
import com.openmpy.taleswiki.board.dto.response.BoardGetResponse.BoardCommentResponse;
import com.openmpy.taleswiki.board.dto.response.BoardGetsResponse;
import com.openmpy.taleswiki.common.dto.PaginatedResponse;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.member.domain.entity.Member;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BoardCustomRepositoryImpl implements BoardCustomRepository {

    private static final String IMAGE_URL_PATTERN = "!\\[[^]]*]\\(https?://[^)]+\\.(webp|png|jpg|jpeg|gif|bmp|svg)\\)";
    private static final Pattern IMAGE_PATTERN = Pattern.compile(IMAGE_URL_PATTERN);

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PaginatedResponse<BoardGetsResponse> gets(final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        final QBoard board = QBoard.board;
        final QBoardComment comment = QBoardComment.boardComment;
        final QBoardLike like = QBoardLike.boardLike;
        final QBoardUnlike unlike = QBoardUnlike.boardUnlike;

        final List<Tuple> tuples = jpaQueryFactory
                .select(
                        board.id,
                        board.title,
                        board.author,
                        board.createdAt,
                        board.view,
                        board.content,
                        like.countDistinct(),
                        unlike.countDistinct(),
                        comment.countDistinct()
                )
                .from(board)
                .leftJoin(like).on(like.board.eq(board))
                .leftJoin(unlike).on(unlike.board.eq(board))
                .leftJoin(comment).on(comment.board.eq(board))
                .groupBy(board.id)
                .orderBy(board.createdAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        final List<BoardGetsResponse> responses = tuples.stream()
                .map(tuple -> {
                    final Long boardId = tuple.get(board.id);
                    final String title = Objects.requireNonNull(tuple.get(board.title)).getValue();
                    final String author = Objects.requireNonNull(tuple.get(board.author)).getValue();
                    final LocalDateTime createdAt = tuple.get(board.createdAt);
                    final long view = Objects.requireNonNull(tuple.get(board.view)).getValue();
                    final String content = Objects.requireNonNull(tuple.get(board.content)).getValue();
                    int likeCount = Objects.requireNonNull(tuple.get(like.countDistinct())).intValue();
                    int unlikeCount = Objects.requireNonNull(tuple.get(unlike.countDistinct())).intValue();
                    int commentCount = Objects.requireNonNull(tuple.get(comment.countDistinct())).intValue();
                    boolean hasImage = IMAGE_PATTERN.matcher(content).find();

                    return new BoardGetsResponse(
                            boardId,
                            title,
                            author,
                            createdAt,
                            view,
                            likeCount - unlikeCount,
                            hasImage,
                            commentCount
                    );
                })
                .toList();

        final Long total = jpaQueryFactory
                .select(board.count())
                .from(board)
                .fetchOne();

        return PaginatedResponse.of(new PageImpl<>(responses, pageRequest, total));
    }

    @Override
    public BoardGetResponse get(final Long boardId) {
        final QBoard board = QBoard.board;
        final QBoardComment comment = QBoardComment.boardComment;
        final QBoardLike like = QBoardLike.boardLike;
        final QBoardUnlike unlike = QBoardUnlike.boardUnlike;

        final Board foundBoard = jpaQueryFactory.selectFrom(board)
                .leftJoin(board.comments, comment).fetchJoin()
                .where(board.id.eq(boardId))
                .fetchOne();

        if (foundBoard == null) {
            throw new CustomException("게시글을 찾을 수 없습니다.");
        }

        final int likeCount = Objects.requireNonNull(jpaQueryFactory.select(like.count())
                .from(like)
                .where(like.board.id.eq(boardId))
                .fetchOne()).intValue();

        final int unlikeCount = Objects.requireNonNull(jpaQueryFactory.select(unlike.count())
                .from(unlike)
                .where(unlike.board.id.eq(boardId))
                .fetchOne()).intValue();

        final List<BoardCommentResponse> commentResponses = foundBoard.getComments().stream()
                .map(BoardGetResponse.BoardCommentResponse::of)
                .toList();

        return new BoardGetResponse(
                foundBoard.getId(),
                foundBoard.getTitle(),
                foundBoard.getAuthor(),
                foundBoard.getContent(),
                foundBoard.getCreatedAt(),
                foundBoard.getView(),
                likeCount - unlikeCount,
                likeCount,
                unlikeCount,
                foundBoard.getMember().getId(),
                commentResponses
        );
    }

    @Override
    public PaginatedResponse<AdminGetBoardsResponse> getsAdmin(final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        final QBoard board = QBoard.board;

        final List<Tuple> tuples = jpaQueryFactory
                .select(
                        board.id,
                        board.author,
                        board.ip,
                        board.title,
                        board.createdAt
                )
                .from(board)
                .orderBy(board.createdAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        final List<AdminGetBoardsResponse> responses = tuples.stream()
                .map(tuple -> {
                    final Long boardId = tuple.get(board.id);
                    final String author = Objects.requireNonNull(tuple.get(board.author)).getValue();
                    final String ip = Objects.requireNonNull(tuple.get(board.ip)).getValue();
                    final String title = Objects.requireNonNull(tuple.get(board.title)).getValue();
                    final LocalDateTime createdAt = tuple.get(board.createdAt);

                    return new AdminGetBoardsResponse(
                            boardId, author, ip, title, createdAt
                    );
                })
                .toList();

        final Long total = jpaQueryFactory
                .select(board.count())
                .from(board)
                .fetchOne();

        return PaginatedResponse.of(new PageImpl<>(responses, pageRequest, total));
    }

    @Override
    public PaginatedResponse<BoardGetsResponse> getsOfMember(final Member member, final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());

        final QBoard board = QBoard.board;
        final QBoardComment comment = QBoardComment.boardComment;
        final QBoardLike like = QBoardLike.boardLike;
        final QBoardUnlike unlike = QBoardUnlike.boardUnlike;

        final List<Tuple> tuples = jpaQueryFactory
                .select(
                        board.id,
                        board.title,
                        board.author,
                        board.createdAt,
                        board.view,
                        board.content,
                        like.countDistinct(),
                        unlike.countDistinct(),
                        comment.countDistinct()
                )
                .from(board)
                .leftJoin(like).on(like.board.eq(board))
                .leftJoin(unlike).on(unlike.board.eq(board))
                .leftJoin(comment).on(comment.board.eq(board))
                .where(board.member.eq(member))
                .groupBy(board.id)
                .orderBy(board.createdAt.desc())
                .offset(pageRequest.getOffset())
                .limit(pageRequest.getPageSize())
                .fetch();

        final List<BoardGetsResponse> responses = tuples.stream()
                .map(tuple -> {
                    final Long boardId = tuple.get(board.id);
                    final String title = Objects.requireNonNull(tuple.get(board.title)).getValue();
                    final String author = Objects.requireNonNull(tuple.get(board.author)).getValue();
                    final LocalDateTime createdAt = tuple.get(board.createdAt);
                    final long view = Objects.requireNonNull(tuple.get(board.view)).getValue();
                    final String content = Objects.requireNonNull(tuple.get(board.content)).getValue();
                    int likeCount = Objects.requireNonNull(tuple.get(like.countDistinct())).intValue();
                    int unlikeCount = Objects.requireNonNull(tuple.get(unlike.countDistinct())).intValue();
                    int commentCount = Objects.requireNonNull(tuple.get(comment.countDistinct())).intValue();
                    boolean hasImage = IMAGE_PATTERN.matcher(content).find();

                    return new BoardGetsResponse(
                            boardId,
                            title,
                            author,
                            createdAt,
                            view,
                            likeCount - unlikeCount,
                            hasImage,
                            commentCount
                    );
                })
                .toList();

        final Long total = jpaQueryFactory
                .select(board.count())
                .from(board)
                .where(board.member.eq(member))
                .fetchOne();

        return PaginatedResponse.of(new PageImpl<>(responses, pageRequest, total));
    }
}
