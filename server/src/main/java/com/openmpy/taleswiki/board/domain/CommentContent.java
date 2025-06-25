package com.openmpy.taleswiki.board.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class CommentContent {

    private String value;

    public CommentContent(final String value) {
        this.value = value;
    }
}
