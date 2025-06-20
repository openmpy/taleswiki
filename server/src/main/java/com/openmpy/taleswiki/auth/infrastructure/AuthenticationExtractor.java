package com.openmpy.taleswiki.auth.infrastructure;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationExtractor {

    public String extract(final HttpServletRequest servletRequest, final String cookieName) {
        final Cookie[] cookies = servletRequest.getCookies();

        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(it -> cookieName.equals(it.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }
}
