package com.openmpy.taleswiki.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class IpAddressUtilTest {

    @DisplayName("[통과] 클라이언트 IP 값을 가져온다.")
    @Test
    void ip_address_test_01() {
        // given
        final MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader("X-Forwarded-For", "127.0.0.1");

        // when
        final String clientIp = IpAddressUtil.getClientIp(servletRequest);

        // then
        assertThat(clientIp).isEqualTo("127.0.0.1");
    }

    @DisplayName("[통과] 요청 헤더에서 IP 값을 가져오지 못했을 경우 RemoteAddr에서 가져온다.")
    @Test
    void ip_address_test_02() {
        // given
        final MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.addHeader("X-Forwarded-For", "");
        servletRequest.setRemoteAddr("127.0.0.1");

        // when
        final String clientIp = IpAddressUtil.getClientIp(servletRequest);

        // then
        assertThat(clientIp).isEqualTo("127.0.0.1");
    }
}