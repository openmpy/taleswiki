package com.openmpy.taleswiki.dictionary.domain;

import com.openmpy.taleswiki.common.exception.CustomException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class DictionaryView {

    private long value;

    public DictionaryView(final long value) {
        validateSize(value);

        this.value = value;
    }

    private void validateSize(final long value) {
        if (value < 0) {
            throw new CustomException("조회수가 음수일 수 없습니다.");
        }
    }

    public void increment(final long value) {
        this.value += value;
    }
}
