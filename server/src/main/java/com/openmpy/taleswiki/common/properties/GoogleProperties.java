package com.openmpy.taleswiki.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("google")
public record GoogleProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        String oAuthTokenUri,
        String grantType
) {
}
