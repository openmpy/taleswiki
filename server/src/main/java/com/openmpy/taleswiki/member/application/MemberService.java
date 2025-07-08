package com.openmpy.taleswiki.member.application;

import com.openmpy.taleswiki.auth.JwtTokenProvider;
import com.openmpy.taleswiki.common.exception.AuthenticationException;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.member.domain.constants.MemberAuthority;
import com.openmpy.taleswiki.member.domain.constants.MemberSocial;
import com.openmpy.taleswiki.member.domain.entity.Member;
import com.openmpy.taleswiki.member.domain.repository.MemberRepository;
import com.openmpy.taleswiki.member.dto.MemberChangeNicknameRequest;
import com.openmpy.taleswiki.member.dto.MemberLoginResponse;
import com.openmpy.taleswiki.member.dto.MemberResponse;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public MemberLoginResponse join(final String email, final MemberSocial memberSocial) {
        final Optional<Member> optionalMember = memberRepository.findByEmail_Value(email);

        if (optionalMember.isPresent()) {
            return MemberLoginResponse.of(optionalMember.get());
        }

        final Member newMember = Member.create(email, "테붕이", memberSocial);
        final Member savedMember = memberRepository.save(newMember);
        return MemberLoginResponse.of(savedMember);
    }

    @Transactional(readOnly = true)
    public MemberResponse me(final Long memberId) {
        final Member member = getMember(memberId);
        return MemberResponse.of(member);
    }

    @Transactional
    public void changeNickname(final Long memberId, final MemberChangeNicknameRequest request) {
        final Member member = getMember(memberId);
        checkNickname(request);

        member.changeNickname(request.nickname());
    }

    public void validateAdmin(final Long memberId) {
        final Member member = getMember(memberId);

        if (!member.getAuthority().equals(MemberAuthority.ADMIN)) {
            throw new AuthenticationException("어드민 권한이 존재하지 않습니다.");
        }
    }

    public String generateToken(final MemberLoginResponse response) {
        final Map<String, Object> payload = Map.of("id", response.id());
        return jwtTokenProvider.createToken(payload);
    }

    public Member getMember(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException("찾을 수 없는 회원 번호입니다."));
    }

    private void checkNickname(final MemberChangeNicknameRequest request) {
        if (memberRepository.existsByNickname_Value(request.nickname())) {
            throw new CustomException("이미 존재하는 닉네임입니다.");
        }
    }
}
