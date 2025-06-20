package com.openmpy.taleswiki.member.dto;

import com.openmpy.taleswiki.member.domain.entity.Member;

public record MemberLoginResponse(
        Long id,
        String email
) {

    public static MemberLoginResponse of(final Member member) {
        return new MemberLoginResponse(member.getId(), member.getEmail());
    }
}
