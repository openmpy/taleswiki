package com.openmpy.taleswiki.common.domain;

import com.openmpy.taleswiki.common.exception.CustomException;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class ClientIp {

    private static final String IP_V4_PATTERN = "^(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}$";
    private static final String IP_V6_PATTERN = "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$" +
            "|^(?:[0-9a-fA-F]{1,4}:){1,7}:$" +
            "|^:(:[0-9a-fA-F]{1,4}){1,7}$" +
            "|^(?:[0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}$" +
            "|^(?:[0-9a-fA-F]{1,4}:){1,5}(?::[0-9a-fA-F]{1,4}){1,2}$" +
            "|^(?:[0-9a-fA-F]{1,4}:){1,4}(?::[0-9a-fA-F]{1,4}){1,3}$" +
            "|^(?:[0-9a-fA-F]{1,4}:){1,3}(?::[0-9a-fA-F]{1,4}){1,4}$" +
            "|^(?:[0-9a-fA-F]{1,4}:){1,2}(?::[0-9a-fA-F]{1,4}){1,5}$" +
            "|^[0-9a-fA-F]{1,4}:(?::[0-9a-fA-F]{1,4}){1,6}$" +
            "|^:(?::[0-9a-fA-F]{1,4}){1,7}$";

    private String value;

    public ClientIp(final String value) {
        validateBlank(value);
        validateIp(value);

        this.value = value;
    }

    private void validateBlank(final String value) {
        if (value == null || value.isBlank()) {
            throw new CustomException("IP 값이 공백일 수 없습니다.");
        }
    }

    private void validateIp(final String value) {
        if (!isValidIpV4(value) && !isValidIpV6(value)) {
            throw new CustomException("IP 값이 올바르지 않습니다.");
        }
    }

    private boolean isValidIpV4(final String value) {
        return Pattern.matches(IP_V4_PATTERN, value);
    }

    private boolean isValidIpV6(final String value) {
        return Pattern.matches(IP_V6_PATTERN, value);
    }
}
