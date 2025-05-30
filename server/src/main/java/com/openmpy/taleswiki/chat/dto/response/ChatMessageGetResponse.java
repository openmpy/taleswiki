package com.openmpy.taleswiki.chat.dto.response;

import com.openmpy.taleswiki.chat.domain.entity.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageGetResponse(List<ChatMessageItemResponse> messages) {

    public record ChatMessageItemResponse(String message, String nickname, String sessionId, LocalDateTime createdAt) {

    }

    public static ChatMessageGetResponse of(final List<ChatMessage> chatMessages) {
        final List<ChatMessageItemResponse> responses = chatMessages.stream()
                .map(it -> new ChatMessageItemResponse(
                        it.getContent(), it.getNickname(), it.getSessionId(), it.getCreatedAt())
                )
                .toList();

        return new ChatMessageGetResponse(responses);
    }
}
