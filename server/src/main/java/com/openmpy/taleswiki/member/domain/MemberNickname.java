package com.openmpy.taleswiki.member.domain;

import com.openmpy.taleswiki.common.exception.CustomException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class MemberNickname {

    private String value;

    public MemberNickname(final String value) {
        validateBlank(value);
        validateLength(value);

        this.value = value;
    }

    private void validateBlank(final String value) {
        if (value == null || value.isBlank()) {
            throw new CustomException("닉네임이 공백일 수 없습니다.");
        }
    }

    private void validateLength(final String value) {
        if (value.length() < 2 || value.length() > 8) {
            throw new CustomException("닉네임 길이가 올바르지 않습니다. (최소 2자, 최대 8자)");
        }
    }
}
