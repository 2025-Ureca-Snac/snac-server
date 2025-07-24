package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.CustomOAuth2User;
import com.ureca.snac.auth.dto.response.GoogleResponse;
import com.ureca.snac.auth.dto.response.KakaoResponse;
import com.ureca.snac.auth.dto.response.NaverResponse;
import com.ureca.snac.auth.dto.response.OAuth2Response;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.auth.util.JWTUtil;
import com.ureca.snac.member.Member;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;


@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final JWTUtil jwtUtil;
    private final AuthRepository authRepository;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("loadUser 메소드 시작");
        String accessToken = userRequest.getAccessToken().getTokenValue();
        log.info("accessToken: {}", accessToken);

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

        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();
        log.info("provider: {}, providerId: {}", provider, providerId);

        // 1) state 파라미터가 JWT로 디코딩되는지 시도해서 플로우 구분
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String state = request.getParameter("state");
        log.info("state : {}", state);

        String emailFromState = null;
        boolean isLinking = true;
        try {
            emailFromState = jwtUtil.getUsername(state);
            log.info("state 디코딩 성공, 연동 플로우: email={}", emailFromState);
        } catch (JwtException e) {
            log.info("state 디코딩 실패, 로그인 플로우");
            isLinking = false;
        }

        String redisKey = provider + ":" + providerId;
        stringRedisTemplate.opsForValue().set(redisKey, accessToken, Duration.ofMinutes(3));
        log.info("AccessToken Redis 저장: {} = {}", redisKey, accessToken);


        if (isLinking) {
            // 소셜 연동
            // 이미 다른 계정에 해당 providerId가 연결되어 있는지 체크
            Member alreadyLinked = switch (provider) {
                case "naver" -> authRepository.findByNaverId(providerId);
                case "google" -> authRepository.findByGoogleId(providerId);
                default -> authRepository.findByKakaoId(providerId);
            };
            if (alreadyLinked != null) {
                // 이미 다른 계정에 연동된 소셜 계정
                log.info("이미 다른 계정에 연동된 소셜 계정");
                throw new OAuth2AuthenticationException("이미 다른 계정에 연동된 소셜 계정입니다.");
            }

            // 연동 대상 회원 조회 및 ID 업데이트
            Member member = authRepository.findByEmail(emailFromState)
                    .orElseThrow(() -> new OAuth2AuthenticationException("존재하지 않는 회원입니다."));
            member.updateSocialId(provider, providerId);
            authRepository.save(member);
            log.info("social 연동 완료: {} -> {}", member.getEmail(), provider);

            return new CustomOAuth2User(member, registrationId, providerId, oAuth2User.getAttributes());
        }

        // -----------------------------------소셜 로그인 ---------------------------------------
        Member existingMember = switch (provider) {
            case "naver" -> authRepository.findByNaverId(providerId);
            case "google" -> authRepository.findByGoogleId(providerId);
            default -> authRepository.findByKakaoId(providerId);
        };
        if (existingMember != null) {
            log.info("기존 회원 로그인: {}", existingMember.getEmail());
            return new CustomOAuth2User(existingMember, registrationId, providerId, oAuth2User.getAttributes());
        }

        // (필요시!!!!!!!!!!) 신규 회원 가입 로직 추가
        log.info("일치하는 계정이 없음.");
        throw new OAuth2AuthenticationException("일치하는 계정이 없습니다.");
    }
}
