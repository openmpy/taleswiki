package com.openmpy.taleswiki.chat.dto.response;

import com.openmpy.taleswiki.chat.domain.entity.ChatMessage;
import java.time.LocalDateTime;

public record ChatMessageSaveResponse(
        String message, String sessionId, String nickname, LocalDateTime createdAt
) {

    public static ChatMessageSaveResponse of(final ChatMessage savedMessage, final String sessionId) {
        return new ChatMessageSaveResponse(
                savedMessage.getContent(), sessionId, savedMessage.getNickname(), savedMessage.getCreatedAt()
        );
    }
}
