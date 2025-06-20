package com.openmpy.taleswiki.common.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private static final int CONNECT_TIMEOUT_SECONDS = 1;
    private static final int READ_TIMEOUT_SECONDS = 5;

    @Bean
    public RestClient restClient(final RestClient.Builder restClientBuilder) {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS));
        requestFactory.setReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS));
        return restClientBuilder.requestFactory(requestFactory).build();
    }
}
