package com.ureca.snac.auth.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.auth.dto.CustomOAuth2User;
import com.ureca.snac.auth.refresh.Refresh;
import com.ureca.snac.auth.repository.RefreshRepository;
import com.ureca.snac.auth.util.CookieUtil;
import com.ureca.snac.auth.util.JWTUtil;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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

        String email = user.getEmail();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        log.info("사용자 이메일: {}, 권한: {}", email, role);

        log.info("socialToken 생성");
        String socialToken = jwtUtil.createJwtForSocial("social", email, role, provider, providerId, 43200000L);
        log.debug("socialToken 생성 완료: {}", socialToken);

        log.info("토큰 전송 시작");
        response.setHeader(AUTHORIZATION, "Bearer " + socialToken);
        response.sendRedirect("https://seungwoo.i234.me/certification");

        
        log.info("CustomOAuth2SuccessHandler 처리 완료");
    }
}
