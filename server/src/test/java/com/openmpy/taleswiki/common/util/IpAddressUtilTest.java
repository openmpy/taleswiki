package com.openmpy.taleswiki.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class IpAddressUtilTest {

    @DisplayName("[통과] HttpServletRequest에서 IP 값을 가져온다.")
    @Test
    void ip_address_test_01() {
        // given
        final String value = "192.168.0.1";
        final MockHttpServletRequest servletRequest = new MockHttpServletRequest();

        servletRequest.addHeader("X-Forwarded-For", value);

        // when
        final String ip = IpAddressUtil.getClientIp(servletRequest);

        // then
        assertThat(ip).isEqualTo(value);
    }

    @DisplayName("[통과] Header에서 IP 값을 찾지 못했을 경우 RemoteAddr 값을 반환한다.")
    @Test
    void ip_address_test_02() {
        // given
        final String value = "0.0.0.0";
        final MockHttpServletRequest servletRequest = new MockHttpServletRequest();

        servletRequest.setRemoteAddr(value);

        // when
        final String ip = IpAddressUtil.getClientIp(servletRequest);

        // then
        assertThat(ip).isEqualTo(value);
    }
}