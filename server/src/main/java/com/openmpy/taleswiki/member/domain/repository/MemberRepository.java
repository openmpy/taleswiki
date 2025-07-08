package com.openmpy.taleswiki.member.domain.repository;

import com.openmpy.taleswiki.member.domain.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail_Value(final String email);

    boolean existsByNickname_Value(final String nickname);
}
