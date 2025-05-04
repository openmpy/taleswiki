package com.openmpy.taleswiki.common.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpAddressUtil {

    public static String getClientIp(final HttpServletRequest request) {
        final String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (final String header : headers) {
            final String ip = request.getHeader(header);

            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }
}
