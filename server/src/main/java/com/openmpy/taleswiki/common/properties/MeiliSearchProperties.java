package com.openmpy.taleswiki.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("meili")
public record MeiliSearchProperties(String host, String masterKey) {
}
