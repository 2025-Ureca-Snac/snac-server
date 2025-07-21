package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.TokenDto;
import com.ureca.snac.auth.exception.SocialLoginException;
import com.ureca.snac.auth.refresh.Refresh;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.auth.repository.RefreshRepository;
import com.ureca.snac.auth.util.JWTUtil;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.Member;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SocialLoginServiceImpl implements SocialLoginService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final AuthRepository authRepository;

    @Override
    public TokenDto socialLogin(String socialToken) {
        if (socialToken == null) {
            // 소셜 토큰이 없는 경우
            throw new SocialLoginException(BaseCode.SOCIAL_TOKEN_INVALID);
        }

        try {
            jwtUtil.isExpired(socialToken);
        } catch (ExpiredJwtException e) {
            // 토큰이 만료된 경우
            throw new SocialLoginException(BaseCode.TOKEN_EXPIRED);
        }

        // 토큰 카테고리가 social 인지
        String category = jwtUtil.getCategory(socialToken);
        if (!"social".equals(category)) {
            // 유효하지 않은 토큰인 경우
            throw new SocialLoginException(BaseCode.SOCIAL_TOKEN_INVALID);
        }

        String provider = jwtUtil.getProvider(socialToken);
        String providerId = jwtUtil.getProviderId(socialToken);

        Member validateMember = validateMember(provider, providerId);

        if(!validateMember.getEmail().equals(jwtUtil.getUsername(socialToken))){
            throw new SocialLoginException(BaseCode.SOCIAL_TOKEN_INVALID);
        }

        String username = jwtUtil.getUsername(socialToken);
        String role = jwtUtil.getRole(socialToken);

        String newAccess = jwtUtil.createJwt("access", username, role, 43200000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        refreshRepository.save(new Refresh(username, newRefresh));

        return new TokenDto(newAccess, newRefresh);
    }

    private Member validateMember(String provider, String providerId) {
        Member member;
        if (Objects.equals(provider, "google")) {
            member = authRepository.findByGoogleId(providerId);
        } else if (Objects.equals(provider, "naver")) {
            member = authRepository.findByNaverId(providerId);
        } else if (Objects.equals(provider, "kakao")) {
            member = authRepository.findByKakaoId(providerId);
        } else throw new SocialLoginException(BaseCode.SOCIAL_TOKEN_INVALID);

        return member;
    }
}
