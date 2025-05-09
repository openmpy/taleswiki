package com.openmpy.taleswiki.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("image")
public record ImageProperties(
        String uploadPath
) {
}
