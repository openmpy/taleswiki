package com.openmpy.taleswiki.dictionary.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class DictionaryHistoryContent {

    @Lob
    private String value;

    public DictionaryHistoryContent(final String value) {
        this.value = value;
    }
}
