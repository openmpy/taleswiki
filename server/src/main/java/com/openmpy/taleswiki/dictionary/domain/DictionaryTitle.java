package com.openmpy.taleswiki.dictionary.domain;

import com.openmpy.taleswiki.common.exception.CustomException;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class DictionaryTitle {

    private static final int MAX_TITLE_LENGTH = 12;
    private static final String INVALID_TITLE_PATTERN = "^[a-zA-Z0-9가-힣]+$";

    private String value;

    public DictionaryTitle(final String value) {
        validateBlank(value);
        validateLength(value);
        validateTitle(value);

        this.value = value;
    }

    private void validateBlank(final String value) {
        if (value == null || value.isBlank()) {
            throw new CustomException("제목이 공백일 수 없습니다.");
        }
    }

    private void validateLength(final String value) {
        if (value.length() > MAX_TITLE_LENGTH) {
            throw new CustomException("제목 길이가 12자를 넘길 수 없습니다.");
        }
    }

    private void validateTitle(final String value) {
        if (!isValidTitle(value)) {
            throw new CustomException("제목이 올바르지 않습니다.");
        }
    }

    private boolean isValidTitle(final String value) {
        return Pattern.matches(INVALID_TITLE_PATTERN, value);
    }
}
