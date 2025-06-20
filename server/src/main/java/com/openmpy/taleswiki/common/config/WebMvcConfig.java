package com.openmpy.taleswiki.common.config;

import com.openmpy.taleswiki.admin.domain.repository.BlacklistRepository;
import com.openmpy.taleswiki.auth.JwtTokenProvider;
import com.openmpy.taleswiki.auth.infrastructure.AuthenticationExtractor;
import com.openmpy.taleswiki.auth.infrastructure.AuthenticationPrincipalArgumentResolver;
import com.openmpy.taleswiki.common.infrastructure.CustomHandlerInterceptor;
import com.openmpy.taleswiki.common.properties.CorsProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final BlacklistRepository blacklistRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationExtractor authenticationExtractor;
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

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationPrincipalArgumentResolver());
    }

    @Bean
    public AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver() {
        return new AuthenticationPrincipalArgumentResolver(jwtTokenProvider, authenticationExtractor);
    }
}
