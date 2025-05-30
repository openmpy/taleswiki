package com.openmpy.taleswiki.chat.domain.repository;

import com.openmpy.taleswiki.chat.domain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
