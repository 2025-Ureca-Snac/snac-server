package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.CustomOAuth2User;
import com.ureca.snac.auth.dto.response.GoogleResponse;
import com.ureca.snac.auth.dto.response.KakaoResponse;
import com.ureca.snac.auth.dto.response.NaverResponse;
import com.ureca.snac.auth.dto.response.OAuth2Response;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.auth.util.JWTUtil;
import com.ureca.snac.member.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final JWTUtil jwtUtil;
    private final AuthRepository authRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("loadUser 메소드 시작");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2 사용자 정보: {}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info("registrationId: {}", registrationId);
        OAuth2Response oAuth2Response = switch (registrationId) {
            case "naver" -> new NaverResponse(oAuth2User.getAttributes());
            case "google" -> new GoogleResponse(oAuth2User.getAttributes());
            case "kakao" -> new KakaoResponse(oAuth2User.getAttributes());
            default -> throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: " + registrationId);
        };
        log.info("OAuth2Response: {}", oAuth2Response);

        String provider   = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();
        log.info("provider: {}, providerId: {}", provider, providerId);

        Member existingMember = switch (provider) {
            case "naver" -> authRepository.findByNaverId(providerId);
            case "google" -> authRepository.findByGoogleId(providerId);
            default -> authRepository.findByKakaoId(providerId);
        };
        if (existingMember != null) {
            log.info("기존 회원: {}", existingMember.getEmail());
            return new CustomOAuth2User(existingMember, registrationId, providerId, oAuth2User.getAttributes());
        }else {
            // 예외처리
        }

        // 사용자 정보 state 에서 꺼내서 확인
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String state = request.getParameter("state");
        log.info("state: {}", state);

        String email = jwtUtil.getUsername(state);
        log.info("email from state: {}", email);
        Member member = authRepository.findByEmail(email)
                .orElseThrow(() -> new OAuth2AuthenticationException("존재하지 않는 회원입니다."));
        log.info("회원 발견: {}", member);

        member.updateSocialId(provider, providerId);
        authRepository.save(member);
        log.info("소셜 ID를 업데이트 및 저장");

        log.info("loadUser 메소드 종료");
        return new CustomOAuth2User(member, registrationId, providerId, oAuth2User.getAttributes());
    }
}
