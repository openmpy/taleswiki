package com.openmpy.taleswiki.common.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.http.server.ServerHttpRequest;

public class IpAddressUtil {

    private static final String[] HEADERS = new String[]{
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
    };

    public static String getClientIp(final HttpServletRequest request) {
        for (final String header : HEADERS) {
            final String ip = request.getHeader(header);

            if (ip != null && !ip.isBlank()) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    public static String getClientIp(final ServerHttpRequest request) {
        for (final String header : HEADERS) {
            final List<String> ips = request.getHeaders().get(header);

            if (ips != null && !ips.isEmpty()) {
                String ip = ips.get(0);

                if (ip != null && !ip.isBlank()) {
                    if (ip.contains(",")) {
                        ip = ip.split(",")[0].trim();
                    }
                    return ip;
                }
            }
        }
        request.getRemoteAddress();
        return request.getRemoteAddress().getAddress().getHostAddress();
    }
}
