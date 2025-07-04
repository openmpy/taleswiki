package com.openmpy.taleswiki.admin.dto.response;

import java.time.LocalDateTime;

public record AdminGetBoardsResponse(
        Long boardId,
        String author,
        String ip,
        String title,
        String content,
        LocalDateTime createdAt
) {
}
