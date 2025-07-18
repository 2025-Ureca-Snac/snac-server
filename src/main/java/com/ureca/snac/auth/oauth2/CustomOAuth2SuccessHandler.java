package com.ureca.snac.auth.oauth2;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.auth.refresh.Refresh;
import com.ureca.snac.auth.repository.RefreshRepository;
import com.ureca.snac.auth.util.CookieUtil;
import com.ureca.snac.auth.util.JWTUtil;
import com.ureca.snac.member.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final long ACCESS_TOKEN_EXPIRATION_MS  = 43_200_000L;  // 12시간
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 86_400_000L;  // 24시간

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final SocialAccountService socialAccountService;
    private String frontendUrl = "http://localhost:5500";

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        Object principal = authentication.getPrincipal();
        log.info("onAuthenticationSuccess 진입, principal: {}", principal);

        if (principal instanceof CustomUserDetails userDetails) {
            // 네이버·카카오 OAuth2
            log.info("네이버/카카오 로그인 성공");
            issueTokensAndRedirect(request, response, userDetails.getMember());

        } else if (principal instanceof OidcUser oidcUser) {
            // 구글 OIDC
            log.info("구글 OIDC 로그인 성공");
            String state = request.getParameter("state");
            log.info("state: {}", state);
            boolean linkFlow = socialAccountService.isLinkFlow(state);
            log.info("linkFlow: {}", linkFlow);

            Member member = socialAccountService.process(
                    "google",
                    oidcUser.getAttributes(),
                    state
            );
            log.info("member: {}", member);

            if (linkFlow) {
                // 계정 연동 완료 리다이렉트
                log.info("계정 연동 완료, 마이페이지로 리다이렉트");
                sendRedirectSilently(request, response, "/mypage?link_success=true");
            } else {
                // 일반 소셜 로그인
                log.info("일반 소셜 로그인, 토큰 발급 및 리다이렉트");
                issueTokensAndRedirect(request, response, member);
            }

        } else {
            log.error("지원하지 않는 Principal 타입: {}", principal.getClass().getName());
            sendRedirectSilently(request, response, "/login?error=unsupported_principal");
        }
    }

    private void issueTokensAndRedirect(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Member member) throws IOException {
        log.info("토큰 발급: {}", member.getEmail());

        String accessToken = jwtUtil.createJwt("access", member.getEmail(), member.getRole().name(), ACCESS_TOKEN_EXPIRATION_MS);
        String refreshToken = jwtUtil.createJwt("refresh", member.getEmail(), member.getRole().name(), REFRESH_TOKEN_EXPIRATION_MS);

        refreshRepository.save(new Refresh(member.getEmail(), refreshToken));

        response.addCookie(CookieUtil.createCookie("access", accessToken));
        response.addCookie(CookieUtil.createCookie("refresh", refreshToken));

        sendRedirectSilently(request, response, "/");
    }

    private void sendRedirectSilently(HttpServletRequest request,
                                      HttpServletResponse response,
                                      String path) throws IOException {
        String target = frontendUrl + path;
        getRedirectStrategy().sendRedirect(request, response, target);
    }
}
