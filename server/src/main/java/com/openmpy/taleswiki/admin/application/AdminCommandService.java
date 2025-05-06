package com.openmpy.taleswiki.admin.application;

import com.openmpy.taleswiki.admin.dto.request.AdminSigninRequest;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.common.properties.AdminProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminCommandService {

    private final AdminProperties adminProperties;

    @Transactional
    public String signin(final AdminSigninRequest request) {
        if (!adminProperties.nickname().equals(request.nickname()) ||
                !adminProperties.password().equals(request.password())) {
            throw new CustomException("닉네임 또는 패스워드를 다시 한번 확인해주시길 바랍니다.");
        }
        return adminProperties.token();
    }
}
