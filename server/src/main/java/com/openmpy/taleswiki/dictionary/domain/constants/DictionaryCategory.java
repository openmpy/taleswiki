package com.openmpy.taleswiki.dictionary.domain.constants;

import com.openmpy.taleswiki.common.exception.CustomException;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum DictionaryCategory {

    PERSON("런너"), GUILD("길드");

    private final String value;

    DictionaryCategory(final String value) {
        this.value = value;
    }

    public static DictionaryCategory fromName(final String name) {
        return Arrays.stream(DictionaryCategory.values())
                .filter(it -> it.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new CustomException("찾을 수 없는 카테고리입니다."));
    }
}
