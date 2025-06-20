package com.openmpy.taleswiki.member.application;

import com.openmpy.taleswiki.auth.JwtTokenProvider;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.member.domain.constants.MemberSocial;
import com.openmpy.taleswiki.member.domain.entity.Member;
import com.openmpy.taleswiki.member.domain.repository.MemberRepository;
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

        final Member newMember = Member.create(email, memberSocial);
        final Member savedMember = memberRepository.save(newMember);
        return MemberLoginResponse.of(savedMember);
    }

    @Transactional(readOnly = true)
    public MemberResponse me(final Long memberId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException("찾을 수 없는 회원 번호입니다."));
        return MemberResponse.of(member);
    }

    public String generateToken(final MemberLoginResponse response) {
        final Map<String, Object> payload = Map.of("id", response.id());
        return jwtTokenProvider.createToken(payload);
    }
}
