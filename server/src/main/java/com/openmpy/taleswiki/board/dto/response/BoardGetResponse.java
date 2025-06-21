package com.openmpy.taleswiki.board.dto.response;

import com.openmpy.taleswiki.board.domain.entity.Board;
import java.time.LocalDateTime;

public record BoardGetResponse(
        Long boardId,
        String title,
        String author,
        String content,
        LocalDateTime createdAt,
        Long view
) {

    public static BoardGetResponse of(final Board board) {
        return new BoardGetResponse(
                board.getId(),
                board.getTitle(),
                board.getAuthor(),
                board.getContent(),
                board.getCreatedAt(),
                board.getView()
        );
    }
}
