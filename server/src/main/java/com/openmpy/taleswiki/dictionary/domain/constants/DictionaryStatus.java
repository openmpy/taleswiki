package com.openmpy.taleswiki.dictionary.domain.constants;

import java.util.Arrays;

public enum DictionaryStatus {

    ALL_ACTIVE, READ_ONLY, HIDDEN;

    public static DictionaryStatus fromName(final String name) {
        return Arrays.stream(DictionaryStatus.values())
                .filter(it -> it.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 상태입니다."));
    }
}
