package com.openmpy.taleswiki.member.domain.constants;

import lombok.Getter;

@Getter
public enum MemberAuthority {

    MEMBER("회원"), ADMIN("관리자");

    private final String value;

    MemberAuthority(final String value) {
        this.value = value;
    }
}
