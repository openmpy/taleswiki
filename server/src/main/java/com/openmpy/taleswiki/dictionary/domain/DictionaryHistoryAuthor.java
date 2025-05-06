package com.openmpy.taleswiki.dictionary.domain;

import com.openmpy.taleswiki.common.exception.CustomException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class DictionaryHistoryAuthor {

    private static final int MAX_AUTHOR_LENGTH = 8;

    private String value;

    public DictionaryHistoryAuthor(final String value) {
        validateBlank(value);
        validateLength(value);

        this.value = value;
    }

    private void validateBlank(final String value) {
        if (value == null || value.isBlank()) {
            throw new CustomException("작성자가 공백일 수 없습니다.");
        }
    }

    private void validateLength(final String value) {
        if (value.length() > MAX_AUTHOR_LENGTH) {
            throw new CustomException("작성자명이 8자를 넘어갈 수 없습니다.");
        }
    }
}
