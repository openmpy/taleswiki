package com.openmpy.taleswiki.member.dto;

import com.openmpy.taleswiki.member.domain.entity.Member;
import java.time.LocalDateTime;

public record MemberResponse(
        Long memberId,
        String email,
        String nickname,
        String social,
        String authority,
        LocalDateTime createdAt
) {

    public static MemberResponse of(final Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getSocial().getValue(),
                member.getAuthority().getValue(),
                member.getCreatedAt()
        );
    }
}
