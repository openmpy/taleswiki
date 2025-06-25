package com.openmpy.taleswiki.board.dto.response;

import com.openmpy.taleswiki.board.domain.entity.Board;
import com.openmpy.taleswiki.board.domain.entity.BoardComment;
import java.time.LocalDateTime;
import java.util.List;

public record BoardGetResponse(
        Long boardId,
        String title,
        String author,
        String content,
        LocalDateTime createdAt,
        Long view,
        Integer likes,
        Integer like,
        Integer unlike,
        Long memberId,
        List<BoardCommentResponse> comments
) {

    public record BoardCommentResponse(
            Long commentId,
            String author,
            String content,
            LocalDateTime createdAt,
            Long memberId
    ) {

        public static BoardCommentResponse of(final BoardComment comment) {
            return new BoardCommentResponse(
                    comment.getId(),
                    comment.getAuthor(),
                    comment.getContent(),
                    comment.getCreatedAt(),
                    comment.getMember().getId()
            );
        }
    }

    public static BoardGetResponse of(final Board board) {
        final List<BoardCommentResponse> comments = board.getComments().stream()
                .map(BoardCommentResponse::of)
                .toList();

        return new BoardGetResponse(
                board.getId(),
                board.getTitle(),
                board.getAuthor(),
                board.getContent(),
                board.getCreatedAt(),
                board.getView(),
                board.getLikes().size() - board.getUnlikes().size(),
                board.getLikes().size(),
                board.getUnlikes().size(),
                board.getMember().getId(),
                comments
        );
    }
}
