package com.openmpy.taleswiki.admin.dto.response;

import java.time.LocalDateTime;

public record AdminGetChatsResponse(
        Long chatId,
        String sender,
        String content,
        String nickname,
        LocalDateTime createdAt
) {
}
