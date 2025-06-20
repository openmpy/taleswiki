package com.openmpy.taleswiki.member.application;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.openmpy.taleswiki.common.exception.CustomException;
import com.openmpy.taleswiki.common.properties.KakaoProperties;
import com.openmpy.taleswiki.member.domain.constants.MemberSocial;
import com.openmpy.taleswiki.member.dto.KakaoLoginResponse;
import com.openmpy.taleswiki.member.dto.MemberLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class KakaoService {

    private final MemberService memberService;
    private final KakaoProperties kakaoProperties;
    private final RestClient restClient;

    public MemberLoginResponse login(final String code) {
        final KakaoLoginResponse response = getInfo(code);
        final String idToken = response.idToken();
        final DecodedJWT decodedJWT = JWT.decode(idToken);
        final String email = decodedJWT.getClaim("email").asString();
        return memberService.join(email, MemberSocial.KAKAO);
    }

    private KakaoLoginResponse getInfo(final String code) {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", kakaoProperties.grantType());
        params.add("client_id", kakaoProperties.clientId());
        params.add("redirect_uri", kakaoProperties.redirectUri());
        params.add("code", code);

        try {
            return restClient.post()
                    .uri(kakaoProperties.oAuthTokenUri())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(params)
                    .retrieve()
                    .body(KakaoLoginResponse.class);
        } catch (final Exception e) {
            throw new CustomException("카카오 로그인에 실패했습니다.");
        }
    }
}
