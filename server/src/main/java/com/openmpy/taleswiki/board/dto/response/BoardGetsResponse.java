package com.openmpy.taleswiki.board.dto.response;

import java.time.LocalDateTime;

public record BoardGetsResponse(
        Long boardId,
        String title,
        String nickname,
        LocalDateTime createdAt,
        Long view,
        Boolean hasImage
) {
}
