package com.openmpy.taleswiki.member.domain;

import com.openmpy.taleswiki.common.exception.CustomException;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class MemberEmail {

    private static final String INVALID_EMAIL_PATTERN = "^(?![\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$).*$";

    private String value;

    public MemberEmail(final String value) {
        validateBlank(value);
        validateEmail(value);

        this.value = value;
    }

    private void validateBlank(final String value) {
        if (value == null || value.isBlank()) {
            throw new CustomException("이메일이 공백일 수 없습니다.");
        }
    }

    private void validateEmail(final String value) {
        if (isValidEmail(value)) {
            throw new CustomException("이메일 형식이 올바르지 않습니다.");
        }
    }

    private boolean isValidEmail(final String value) {
        return Pattern.matches(INVALID_EMAIL_PATTERN, value);
    }
}
