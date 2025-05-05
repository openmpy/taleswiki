package com.openmpy.taleswiki.common.domain;

import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ClientIp {

    private static final String INVALID_IP_V4_PATTERN = "^((25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.|$)){4}$";

    private String value;

    public ClientIp(final String value) {
        validateBlank(value);
//        validateIp(value);

        this.value = value;
    }

    private void validateBlank(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("IP 값이 공백일 수 없습니다.");
        }
    }

    private void validateIp(final String value) {
        if (!isValidIpV4(value)) {
            throw new IllegalArgumentException("IP 값이 올바르지 않습니다.");
        }
    }

    private boolean isValidIpV4(final String value) {
        return Pattern.matches(INVALID_IP_V4_PATTERN, value);
    }
}
