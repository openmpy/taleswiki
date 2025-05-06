package com.openmpy.taleswiki.dictionary.domain;

import com.openmpy.taleswiki.common.exception.CustomException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class DictionaryHistoryVersion {

    private long value;

    public DictionaryHistoryVersion(final long value) {
        validateVersion(value);

        this.value = value;
    }

    private void validateVersion(final long value) {
        if (value <= 0) {
            throw new CustomException("버전 값이 0 또는 음수일 수 없습니다.");
        }
    }
}
