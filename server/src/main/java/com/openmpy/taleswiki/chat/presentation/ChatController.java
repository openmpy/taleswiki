package com.openmpy.taleswiki.chat.presentation;

import com.openmpy.taleswiki.chat.application.ChatService;
import com.openmpy.taleswiki.chat.dto.request.ChatMessageSaveRequest;
import com.openmpy.taleswiki.chat.dto.response.ChatMessageGetResponse;
import com.openmpy.taleswiki.chat.dto.response.ChatMessageSaveResponse;
import com.openmpy.taleswiki.common.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/chats")
@RestController
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat")
    @SendTo("/topic/public")
    public ChatMessageSaveResponse sendMessage(
            @Payload final ChatMessageSaveRequest request,
            final SimpMessageHeaderAccessor simpMessageHeaderAccessor
    ) {
        return chatService.sendMessage(request, simpMessageHeaderAccessor);
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public ErrorResponse handleWebSocketException(final Exception exception) {
        return new ErrorResponse(exception.getMessage());
    }

    @GetMapping
    public ResponseEntity<ChatMessageGetResponse> getChatMessages() {
        final ChatMessageGetResponse response = chatService.getMessages();
        return ResponseEntity.ok(response);
    }
}
