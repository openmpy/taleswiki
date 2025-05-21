package com.openmpy.taleswiki.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("s3")
public record S3Properties(String endpoint, String region, String accessKey, String secretKey) {
}
