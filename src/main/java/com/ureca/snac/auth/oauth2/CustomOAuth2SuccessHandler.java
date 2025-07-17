package com.ureca.snac.auth.oauth2;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.auth.refresh.Refresh;
import com.ureca.snac.auth.repository.AuthRepository;
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
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final long ACCESS_TOKEN_EXPIRATION_MS = 43200000L;  // 12시간
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 86400000L;  // 24시간


    private String frontendUrl="http://localhost:5500";

    private final JWTUtil jwtUtil;
    private final AuthRepository authRepository;
    private final RefreshRepository refreshRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        log.info("인증 성공. Principal 타입: {}",
                authentication.getPrincipal().getClass().getSimpleName());

        Object principal = authentication.getPrincipal();
        if (principal instanceof OidcUser oidcUser) {
            handleOidcFlow(request, response, oidcUser);
        } else if (principal instanceof CustomUserDetails userDetails) {
            issueTokensAndRedirect(request, response, userDetails.getMember());
        } else {
            log.error("지원하지 않는 Principal 타입: {}", principal.getClass().getName());
            sendRedirectSilently(request, response, "/login?error=unsupported_principal");
        }
    }

    private void handleOidcFlow(HttpServletRequest request,
                                HttpServletResponse response,
                                OidcUser oidcUser) throws IOException {
        String state = request.getParameter("state");
        log.debug("OIDC 로그인 시 state: {}", state);

        if (isAccountLinkFlow(state)) {
            linkAccount(request, response, state, oidcUser);
        } else {
            socialLogin(request, response, "google", oidcUser.getSubject());
        }
    }

    private boolean isAccountLinkFlow(String state) {
        if (!StringUtils.hasText(state)) return false;
        try {
            return "link".equals(jwtUtil.getCategory(state))
                    && !jwtUtil.isExpired(state);
        } catch (Exception e) {
            log.debug("연동 토큰 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    private void linkAccount(HttpServletRequest request,
                             HttpServletResponse response,
                             String state,
                             OidcUser oidcUser){
        String email = jwtUtil.getUsername(state);
        authRepository.findByEmail(email).ifPresentOrElse(member -> {
            if (authRepository.findByGoogleId(oidcUser.getSubject()).isPresent()) {
                log.warn("Google ID({})가 이미 연동되어 있습니다.", oidcUser.getSubject());
                try {
                    sendRedirectSilently(request, response, "/login?error=already_linked");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                member.updateSocialId("google", oidcUser.getSubject());
                log.info("Google ID({})가 사용자({})에 연동되었습니다.", oidcUser.getSubject(), email);
                try {
                    sendRedirectSilently(request, response, "/mypage?link_success=true");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, () -> {
            log.error("연동 토큰의 이메일({})에 해당하는 사용자가 없습니다.", email);
            try {
                sendRedirectSilently(request, response, "/login?error=user_not_found");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void socialLogin(HttpServletRequest request,
                             HttpServletResponse response,
                             String provider,
                             String providerId) throws IOException {
        authRepository.findByGoogleId(providerId).ifPresentOrElse(member -> {
            try {
                issueTokensAndRedirect(request, response, member);
            } catch (IOException e) {
                log.error("토큰 발급 중 예외 발생", e);
            }
        }, () -> {
            log.warn("연동되지 않은 {} 로그인 시도: {}", provider, providerId);
            try {
                sendRedirectSilently(request, response, "/login?error=unlinked_account");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void issueTokensAndRedirect(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Member member) throws IOException {
        log.info("사용자({})에 대해 토큰 발급 시작", member.getEmail());

        String accessToken = jwtUtil.createJwt("access", member.getEmail(), member.getRole().name(), ACCESS_TOKEN_EXPIRATION_MS);
        String refreshToken = jwtUtil.createJwt("refresh", member.getEmail(), member.getRole().name(), REFRESH_TOKEN_EXPIRATION_MS);

        refreshRepository.save(new Refresh(member.getEmail(), refreshToken));
        log.debug("리프레시 토큰 저장 완료");

        response.addCookie(CookieUtil.createCookie("access", accessToken));
        response.addCookie(CookieUtil.createCookie("refresh", refreshToken));
        log.debug("쿠키에 토큰 추가 완료");

        sendRedirectSilently(request, response, "/");
    }

    private void sendRedirectSilently(HttpServletRequest request,
                                      HttpServletResponse response,
                                      String path) throws IOException {
        String target = frontendUrl + path;
        log.debug("리다이렉트 대상: {}", target);
        getRedirectStrategy().sendRedirect(request, response, target);
    }
}