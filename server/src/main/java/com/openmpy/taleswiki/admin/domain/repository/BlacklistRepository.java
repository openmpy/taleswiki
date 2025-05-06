package com.openmpy.taleswiki.admin.domain.repository;

import com.openmpy.taleswiki.admin.domain.entity.Blacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistRepository extends JpaRepository<Blacklist, Long> {

    boolean existsByIp_Value(final String ip);
}
