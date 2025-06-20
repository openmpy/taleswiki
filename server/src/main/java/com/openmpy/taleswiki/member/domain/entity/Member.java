package com.openmpy.taleswiki.member.domain.entity;

import com.openmpy.taleswiki.common.domain.entity.BaseEntity;
import com.openmpy.taleswiki.member.domain.MemberEmail;
import com.openmpy.taleswiki.member.domain.constants.MemberSocial;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false))
    private MemberEmail email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberSocial social;

    @Builder
    public Member(final String email, final MemberSocial social) {
        this.email = new MemberEmail(email);
        this.social = social;
    }

    public static Member create(final String email, final MemberSocial memberSocial) {
        return Member.builder()
                .email(email)
                .social(memberSocial)
                .build();
    }

    public String getEmail() {
        return email.getValue();
    }
}
