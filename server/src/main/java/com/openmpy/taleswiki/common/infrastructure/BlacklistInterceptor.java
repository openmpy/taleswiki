package com.openmpy.taleswiki.common.infrastructure;

import com.openmpy.taleswiki.admin.domain.repository.BlacklistRepository;
import com.openmpy.taleswiki.common.util.IpAddressUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
public class BlacklistInterceptor implements HandlerInterceptor {

    private final BlacklistRepository blacklistRepository;

    @Override
    public boolean preHandle(
            final HttpServletRequest request, final HttpServletResponse response, final Object handler
    ) throws Exception {
        final String ip = IpAddressUtil.getClientIp(request);

        if (blacklistRepository.existsByIp_Value(ip)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "블랙리스트에 등록된 IP입니다.");
            return false;
        }
        return true;
    }
}
