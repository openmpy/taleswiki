package com.openmpy.taleswiki.common.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.taleswiki.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ClientIpTest {

    @DisplayName("[통과] 클라이언트 IP 클래스를 생성한다.")
    @ParameterizedTest(name = "값: {0}")
    @ValueSource(strings = {"127.0.0.1", "2001:0db8:85a3:0000:0000:8a2e:0370:7334"})
    void client_ip_test_01(final String value) {
        // when
        final ClientIp clientIp = new ClientIp(value);

        // then
        assertThat(clientIp.getValue()).isEqualTo(value);
    }

    @DisplayName("[예외] 클라이언트 IP 값이 공백이다.")
    @ParameterizedTest(name = "값: {0}")
    @NullAndEmptySource
    void 예외_client_ip_test_01(final String value) {
        // when & then
        assertThatThrownBy(() -> new ClientIp(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("IP 값이 공백일 수 없습니다.");
    }

    @DisplayName("[예외] 클라이언트 IP 값이 유효하지 않다.")
    @ParameterizedTest(name = "값: {0}")
    @ValueSource(strings = {"127", "ip", "1:1:1"})
    void 예외_client_ip_test_02(final String value) {
        // when & then
        assertThatThrownBy(() -> new ClientIp(value))
                .isInstanceOf(CustomException.class)
                .hasMessage("IP 값이 올바르지 않습니다.");
    }
}