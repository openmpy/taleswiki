package com.openmpy.taleswiki.dictionary.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class DictionaryHistorySize {

    private long value;

    public DictionaryHistorySize(final long value) {
        validateSize(value);

        this.value = value;
    }

    private void validateSize(final long value) {
        if (value < 0) {
            throw new IllegalArgumentException("사이즈 값이 음수일 수 없습니다.");
        }
    }
}
