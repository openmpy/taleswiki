package com.openmpy.taleswiki.chat.domain.entity;

import com.openmpy.taleswiki.chat.domain.ChatMessageContent;
import com.openmpy.taleswiki.chat.domain.ChatMessageNickname;
import com.openmpy.taleswiki.chat.domain.ChatMessageSessionId;
import com.openmpy.taleswiki.common.domain.ClientIp;
import com.openmpy.taleswiki.common.domain.entity.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "ip", nullable = false))
    private ClientIp sender;


    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "content", nullable = false))
    private ChatMessageContent content;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "nickname", nullable = false))
    private ChatMessageNickname nickname;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "sessionId", nullable = false))
    private ChatMessageSessionId sessionId;

    @Builder
    public ChatMessage(final String sender, final String content, final String nickname, final String sessionId) {
        this.sender = new ClientIp(sender);
        this.content = new ChatMessageContent(content);
        this.nickname = new ChatMessageNickname(nickname);
        this.sessionId = new ChatMessageSessionId(sessionId);
    }

    public static ChatMessage create(
            final String sender, final String content, final String nickname, final String sessionId
    ) {
        return ChatMessage.builder()
                .sender(sender)
                .content(content)
                .nickname(nickname)
                .sessionId(sessionId)
                .build();
    }

    public String getSender() {
        return sender.getValue();
    }

    public String getNickname() {
        return nickname.getValue();
    }

    public String getContent() {
        return content.getValue();
    }

    public String getSessionId() {
        return sessionId.getValue();
    }
}
