package com.openmpy.taleswiki.admin.application;

import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.common.properties.AdminProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminQueryService {

    private final AdminProperties adminProperties;

    @Transactional
    public void me(final String token) {
        final String adminToken = adminProperties.token();

        if (token == null || token.isBlank() || !token.equals(adminToken)) {
            throw new CustomException("잘못된 토큰 값입니다.");
        }
    }
}
