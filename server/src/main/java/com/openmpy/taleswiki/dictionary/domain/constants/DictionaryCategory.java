package com.openmpy.taleswiki.dictionary.domain.constants;

import lombok.Getter;

@Getter
public enum DictionaryCategory {

    PERSON("인물"), GUILD("길드");

    private final String value;

    DictionaryCategory(final String value) {
        this.value = value;
    }
}
