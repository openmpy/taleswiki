package com.openmpy.taleswiki.common.config;

import com.openmpy.taleswiki.admin.domain.repository.BlacklistRepository;
import com.openmpy.taleswiki.common.infrastructure.CustomHandlerInterceptor;
import com.openmpy.taleswiki.common.properties.CorsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final BlacklistRepository blacklistRepository;
    private final CorsProperties corsProperties;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(blacklistInterceptor())
                .addPathPatterns("/api/v1/**");
    }

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping(corsProperties.pathPattern())
                .allowedOrigins(corsProperties.origins())
                .allowedMethods(corsProperties.methods())
                .allowedHeaders(corsProperties.headers())
                .allowCredentials(corsProperties.allowCredentials())
                .maxAge(corsProperties.maxAge());
    }

    @Bean
    public CustomHandlerInterceptor blacklistInterceptor() {
        return new CustomHandlerInterceptor(blacklistRepository);
    }
}
