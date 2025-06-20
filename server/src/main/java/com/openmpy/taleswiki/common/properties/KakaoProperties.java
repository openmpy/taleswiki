package com.openmpy.taleswiki.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kakao")
public record KakaoProperties(
        String clientId,
        String redirectUri,
        String oAuthTokenUri,
        String grantType
) {
}
