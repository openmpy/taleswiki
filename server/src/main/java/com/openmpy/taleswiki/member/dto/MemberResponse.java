package com.openmpy.taleswiki.member.dto;

import com.openmpy.taleswiki.member.domain.entity.Member;

public record MemberResponse(
        Long memberId,
        String email,
        String social
) {

    public static MemberResponse of(final Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getSocial().name()
        );
    }
}
