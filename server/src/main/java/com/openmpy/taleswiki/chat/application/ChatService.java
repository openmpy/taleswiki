package com.openmpy.taleswiki.chat.application;

import com.openmpy.taleswiki.chat.domain.entity.ChatMessage;
import com.openmpy.taleswiki.chat.domain.repository.ChatMessageRepository;
import com.openmpy.taleswiki.chat.dto.request.ChatMessageSaveRequest;
import com.openmpy.taleswiki.chat.dto.response.ChatMessageGetResponse;
import com.openmpy.taleswiki.chat.dto.response.ChatMessageSaveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatMessageSaveResponse sendMessage(
            final ChatMessageSaveRequest request, final SimpMessageHeaderAccessor simpMessageHeaderAccessor
    ) {
        final String clientIp = (String) simpMessageHeaderAccessor.getSessionAttributes().get("IP_ADDRESS");
        final ChatMessage chatMessage = ChatMessage.create(
                clientIp, request.message(), request.nickname(), request.sessionId()
        );
        final ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        return ChatMessageSaveResponse.of(savedMessage, request.sessionId());
    }

    @Transactional(readOnly = true)
    public ChatMessageGetResponse getMessages() {
        final PageRequest pageRequest = PageRequest.of(0, 100);
        final Page<ChatMessage> paginatedMessages = chatMessageRepository.findAll(pageRequest);
        return ChatMessageGetResponse.of(paginatedMessages.getContent());
    }
}
