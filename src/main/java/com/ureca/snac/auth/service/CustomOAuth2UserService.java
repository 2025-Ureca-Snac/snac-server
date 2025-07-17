package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.auth.dto.response.GoogleResponse;
import com.ureca.snac.auth.dto.response.KakaoResponse;
import com.ureca.snac.auth.dto.response.NaverResponse;
import com.ureca.snac.auth.dto.response.OAuth2Response;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.auth.util.JWTUtil;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AuthRepository authRepository;
    private final JWTUtil jwtUtil;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("===== 소셜 로그인 처리 시작 =====");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.debug("OAuth2User 속성: {}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("소셜 공급자: {}", registrationId);

        OAuth2Response oAuth2Response = getOAuth2Response(registrationId, oAuth2User);
        if (oAuth2Response == null) {
            log.error("지원하지 않는 공급자: {}", registrationId);
            throw new OAuth2AuthenticationException("지원하지 않는 로그인 공급자: " + registrationId);
        }

        String linkToken = (String) userRequest.getAdditionalParameters().get("state");
        log.info("state (linkToken): {}", linkToken);

        Member member = isAccountLinkFlow(linkToken)
                ? linkAccount(linkToken, oAuth2Response)
                : processSocialLogin(oAuth2Response);

        log.info("소셜 로그인 처리 완료. 사용자 이메일: {}", member.getEmail());
        log.info("===== CustomOAuth2UserService 처리 종료 =====");

        return new CustomUserDetails(member, oAuth2User.getAttributes());
    }

    private boolean isAccountLinkFlow(String linkToken) {
        if (!StringUtils.hasText(linkToken)) {
            log.debug("state가 비어있거나 null입니다.");
            return false;
        }
        try {
            boolean isLink = "link".equals(jwtUtil.getCategory(linkToken));
            boolean notExpired = !jwtUtil.isExpired(linkToken);
            log.debug("isAccountLinkFlow - link 카테고리: {}, 만료여부: {}", isLink, !notExpired);
            return isLink && notExpired;
        } catch (Exception e) {
            log.error("linkToken 검증 예외: {}", e.getMessage());
            return false;
        }
    }

    private Member linkAccount(String linkToken, OAuth2Response oAuth2Response) {
        String email = jwtUtil.getUsername(linkToken);
        log.info("링크 토큰에서 추출한 이메일: {}", email);

        if (email == null) {
            log.error("링크 토큰이 유효하지 않거나 이메일이 없습니다.");
            throw new OAuth2AuthenticationException("유효하지 않은 링크 토큰입니다.");
        }

        if (findMemberByOauth(oAuth2Response) != null) {
            log.warn("이미 다른 계정에 연동된 소셜 계정입니다. 공급자: {}, ID: {}",
                    oAuth2Response.getProvider(), oAuth2Response.getProviderId());
            throw new OAuth2AuthenticationException("이미 연동된 소셜 계정입니다.");
        }

        Member member = authRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("이메일({})에 해당하는 사용자를 찾을 수 없습니다.", email);
                    return new MemberNotFoundException();
                });

        member.updateSocialId(oAuth2Response.getProvider(), oAuth2Response.getProviderId());
        log.info("계정 연동 성공. 사용자 이메일: {}, 공급자: {}", email, oAuth2Response.getProvider());
        return member;
    }

    private Member processSocialLogin(OAuth2Response oAuth2Response) {
        log.info("소셜 로그인 흐름 진입. 공급자: {}, ID: {}",
                oAuth2Response.getProvider(), oAuth2Response.getProviderId());
        Member member = findMemberByOauth(oAuth2Response);
        if (member == null) {
            log.error("가입되지 않은 소셜 계정입니다. 연동을 먼저 진행해주세요. 공급자: {}, ID: {}",
                    oAuth2Response.getProvider(), oAuth2Response.getProviderId());
            throw new OAuth2AuthenticationException("가입되지 않은 소셜 계정입니다.");
        }
        log.info("기존 사용자 로그인 처리. 사용자 ID: {}", member.getId());
        return member;
    }

    private OAuth2Response getOAuth2Response(String registrationId, OAuth2User oAuth2User) {
        return switch (registrationId.toLowerCase()) {
            case "naver"  -> new NaverResponse(oAuth2User.getAttributes());
            case "google" -> new GoogleResponse(oAuth2User.getAttributes());
            case "kakao"  -> new KakaoResponse(oAuth2User.getAttributes());
            default         -> null;
        };
    }

    private Member findMemberByOauth(OAuth2Response oAuth2Response) {
        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();
        log.debug("findMemberByOauth - 공급자: {}, ID: {}", provider, providerId);
        return switch (provider.toLowerCase()) {
            case "naver"  -> authRepository.findByNaverId(providerId).orElse(null);
            case "google" -> authRepository.findByGoogleId(providerId).orElse(null);
            case "kakao"  -> authRepository.findByKakaoId(providerId).orElse(null);
            default         -> null;
        };
    }
}
