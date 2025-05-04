package com.openmpy.taleswiki.common.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ClientIpTest {

    @DisplayName("[통과] ClientIp 클래스를 생성한다.")
    @Test
    void client_ip_test_01() {
        // given
        final String value = "192.168.0.1";

        // when
        final ClientIp ip = new ClientIp(value);

        // then
        assertThat(ip.getValue()).isEqualTo(value);
    }

    @DisplayName("[예외] IP 값이 공백이다.")
    @ParameterizedTest(name = "값: {0}")
    @NullAndEmptySource
    void 예외_client_ip_test_01(final String value) {
        // when & then
        assertThatThrownBy(() -> new ClientIp(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("IP 값이 공백일 수 없습니다.");
    }

    @DisplayName("[예외] IP 값이 IPv4 형식이 아니다.")
    @ParameterizedTest(name = "값: {0}")
    @ValueSource(strings = {"192.168.0.1.1", "192.168.0.1.1.1"})
    void 예외_client_ip_test_02(final String value) {
        // when & then
        assertThatThrownBy(() -> new ClientIp(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("IP 값이 올바르지 않습니다.");
    }
}