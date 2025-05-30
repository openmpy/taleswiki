package com.openmpy.taleswiki.chat.dto.request;

public record ChatMessageSaveRequest(String message, String sessionId, String nickname) {
}
