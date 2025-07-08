package com.openmpy.taleswiki.member.domain.constants;

import lombok.Getter;

@Getter
public enum MemberSocial {

    GOOGLE("구글"),
    KAKAO("카카오");

    private final String value;

    MemberSocial(final String value) {
        this.value = value;
    }
}
