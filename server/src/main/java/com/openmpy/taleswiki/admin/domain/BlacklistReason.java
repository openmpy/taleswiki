package com.openmpy.taleswiki.admin.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class BlacklistReason {

    private String value;

    public BlacklistReason(final String value) {
        this.value = value;
    }
}
