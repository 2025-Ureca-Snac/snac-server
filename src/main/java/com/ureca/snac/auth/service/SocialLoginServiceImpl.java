package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.TokenDto;
import com.ureca.snac.auth.exception.SocialLoginException;
import com.ureca.snac.auth.refresh.Refresh;
import com.ureca.snac.auth.repository.RefreshRepository;
import com.ureca.snac.auth.util.JWTUtil;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.auth.oauth2.SocialProvider;
import com.ureca.snac.member.entity.SocialLink;
import com.ureca.snac.member.repository.SocialLinkRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialLoginServiceImpl implements SocialLoginService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final SocialLinkRepository socialLinkRepository;

    @Override
    public TokenDto socialLogin(String socialToken) {
        log.info("소셜 로그인 요청 시작");
        if (socialToken == null) {
            // 소셜 토큰이 없는 경우
            log.warn("소셜 토큰 누락으로 로그인 실패");
            throw new SocialLoginException(BaseCode.SOCIAL_TOKEN_INVALID);
        }

        try {
            jwtUtil.isExpired(socialToken);
        } catch (JwtException e) {
            // 토큰이 만료된 경우
            log.warn("소셜 토큰 만료: {}", e.getMessage());
            throw new SocialLoginException(BaseCode.TOKEN_EXPIRED);
        }

        // 토큰 카테고리가 social 인지
        String category = jwtUtil.getCategory(socialToken);
        if (!"social".equals(category)) {
            // 유효하지 않은 토큰인 경우
            log.warn("잘못된 토큰 카테고리: {}", category);
            throw new SocialLoginException(BaseCode.SOCIAL_TOKEN_INVALID);
        }

        SocialProvider provider = SocialProvider.fromValue(jwtUtil.getProvider(socialToken));
        String providerId = jwtUtil.getProviderId(socialToken);
        log.info("프로바이더={}, providerId={} 로 멤버 조회 시도", provider, providerId);

        SocialLink socialLink = socialLinkRepository
                .findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() ->{
                    log.warn("DB에 일치하는 소셜 계정 없음: provider={}, providerId={}", provider, providerId);
                    return new SocialLoginException(BaseCode.SOCIAL_TOKEN_INVALID);
                });
        Member member = socialLink.getMember();

        String username = jwtUtil.getUsername(socialToken);
        if(!member.getEmail().equals(username)){
            log.warn("토큰 사용자 불일치: 토큰 username={}, member.email={}", username, member.getEmail());
            throw new SocialLoginException(BaseCode.SOCIAL_TOKEN_INVALID);
        }

        String role = jwtUtil.getRole(socialToken);
        log.info("소셜 로그인 검증 완료: email={}, role={}", username, role);

        String newAccess = jwtUtil.createJwt("access", username, role, 43200000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        refreshRepository.save(new Refresh(username, newRefresh));

        log.info("토큰 재발급 완료: email={}", username);
        return new TokenDto(newAccess, newRefresh);
    }
}
