package com.ureca.snac.auth.oauth2;

import com.ureca.snac.auth.dto.CustomOAuth2User;
import com.ureca.snac.auth.refresh.Refresh;
import com.ureca.snac.auth.repository.RefreshRepository;
import com.ureca.snac.auth.util.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

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

        String email = user.getEmail();
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        log.info("사용자 이메일: {}, 권한: {}", email, role);

        log.info("새로운 JWT 토큰 생성");
        String newAccess = jwtUtil.createJwt("access", email, role, 43200000L);
        String newRefresh = jwtUtil.createJwt("refresh", email, role, 86400000L);
        log.debug("새로운 Access : {}", newAccess);
        log.debug("새로운 Refresh : {}", newRefresh);

        log.info("사용자 '{}'의 Refresh 토큰을 REDIS에 저장.", email);
        refreshRepository.save(new Refresh(email, newRefresh));
        log.info("Refresh 토큰 저장 완료.");

        log.info("토큰 전송 시도");
        response.setHeader("Authorization", "Bearer " + newAccess);
        response.addHeader("refresh", newRefresh);
        response.setStatus(HttpServletResponse.SC_OK);
        log.debug("응답 헤더 설정 완료: Authorization, refresh");
        log.debug("응답 상태 코드 설정 완료: {}", HttpServletResponse.SC_OK);

        response.getWriter().flush();
        response.getWriter().close();

        log.info("CustomOAuth2SuccessHandler 처리 완료");
    }
}
