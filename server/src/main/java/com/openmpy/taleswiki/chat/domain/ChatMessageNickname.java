package com.openmpy.taleswiki.chat.domain;

import com.openmpy.taleswiki.common.exception.CustomException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ChatMessageNickname {

    private static final int MAX_NICKNAME_LENGTH = 16;

    private String value;

    public ChatMessageNickname(final String value) {
        validateBlank(value);
        validateLength(value);

        this.value = value;
    }

    private void validateBlank(final String value) {
        if (value == null || value.isBlank()) {
            throw new CustomException("채팅 닉네임이 빈 값일 수 없습니다.");
        }
    }

    private void validateLength(final String value) {
        if (value.length() > MAX_NICKNAME_LENGTH) {
            throw new CustomException("채팅 닉네임 길이가 16자를 초과할 수 없습니다.");
        }
    }
}
