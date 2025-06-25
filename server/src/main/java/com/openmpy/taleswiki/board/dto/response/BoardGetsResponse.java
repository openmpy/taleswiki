package com.openmpy.taleswiki.board.dto.response;

import java.time.LocalDateTime;

public record BoardGetsResponse(
        Long boardId,
        String title,
        String nickname,
        LocalDateTime createdAt,
        Long view,
        Integer likes,
        Boolean hasImage,
        Integer commentsCount
) {
}
