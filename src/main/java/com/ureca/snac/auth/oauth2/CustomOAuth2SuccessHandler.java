package com.ureca.snac.auth.oauth2;

import com.ureca.snac.auth.dto.CustomOAuth2User;
import com.ureca.snac.auth.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        log.info("OAuth2 로그인 성공. CustomOAuth2SuccessHandler 시작");

        log.debug("요청 URI: {}", request.getRequestURI());
        log.debug("Authentication 객체: {}", authentication);

        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        log.debug("OAuth2User principal: {}", user);

        String provider   = user.getProvider();
        String providerId = user.getProviderId();

        log.info("provider: {}, providerId: {}", provider, providerId);

        String email = user.getEmail();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        log.info("사용자 이메일: {}, 권한: {}", email, role);

        log.info("socialToken 생성");
        String socialToken = jwtUtil.createSocialToken(email, role, provider, providerId);
        log.debug("socialToken 생성 완료: {}", socialToken);

        log.info("리다이렉트 URL 생성 및 토큰 추가");
        String redirectUrl = UriComponentsBuilder.fromUriString("https://snac-app.com/certification")
                .queryParam("social", socialToken)
                .build().toUriString();

        log.info("토큰을 포함하여 리다이렉트: {}", redirectUrl);
        response.sendRedirect(redirectUrl);

        log.info("CustomOAuth2SuccessHandler 처리 완료");
    }
}
