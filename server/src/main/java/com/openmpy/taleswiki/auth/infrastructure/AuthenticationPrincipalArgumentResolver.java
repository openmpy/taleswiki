package com.openmpy.taleswiki.auth.infrastructure;

import com.openmpy.taleswiki.auth.JwtTokenProvider;
import com.openmpy.taleswiki.auth.annotation.Login;
import com.openmpy.taleswiki.common.exception.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class AuthenticationPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationExtractor authenticationExtractor;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Login.class) && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Long resolveArgument(
            final MethodParameter parameter,
            final ModelAndViewContainer mavContainer,
            final NativeWebRequest webRequest,
            final WebDataBinderFactory binderFactory
    ) throws Exception {
        final Login parameterAnnotation = parameter.getParameterAnnotation(Login.class);
        final HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        final String accessToken = authenticationExtractor.extract(servletRequest, "access_token");

        if (parameterAnnotation.isRequired() && accessToken == null) {
            throw new AuthenticationException("쿠키를 찾을 수 없습니다.");
        }
        if (!parameterAnnotation.isRequired() && accessToken == null) {
            return null;
        }
        return jwtTokenProvider.getMemberId(accessToken);
    }
}
