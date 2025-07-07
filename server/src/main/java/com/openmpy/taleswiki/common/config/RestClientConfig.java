package com.openmpy.taleswiki.common.config;

import com.openmpy.taleswiki.common.properties.MeiliSearchProperties;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Configuration
public class RestClientConfig {

    private static final int CONNECT_TIMEOUT_SECONDS = 1;
    private static final int READ_TIMEOUT_SECONDS = 5;

    private final MeiliSearchProperties meiliSearchProperties;

    @Bean
    public RestClient meiliSearchRestClient(final RestClient.Builder restClientBuilder) {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS));
        requestFactory.setReadTimeout(Duration.ofSeconds(READ_TIMEOUT_SECONDS));

        return restClientBuilder
                .requestFactory(requestFactory)
                .baseUrl(meiliSearchProperties.host())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + meiliSearchProperties.masterKey())
                .build();
    }
}
