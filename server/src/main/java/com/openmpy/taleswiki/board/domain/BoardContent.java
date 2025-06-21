package com.openmpy.taleswiki.board.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class BoardContent {

    @Lob
    private String value;

    public BoardContent(final String value) {
        this.value = value;
    }
}
