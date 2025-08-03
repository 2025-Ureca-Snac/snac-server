package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.CustomOAuth2User;
import com.ureca.snac.auth.dto.response.GoogleResponse;
import com.ureca.snac.auth.dto.response.KakaoResponse;
import com.ureca.snac.auth.dto.response.NaverResponse;
import com.ureca.snac.auth.dto.response.OAuth2Response;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.auth.util.JWTUtil;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.auth.oauth2.SocialProvider;
import com.ureca.snac.member.entity.SocialLink;
import com.ureca.snac.member.repository.SocialLinkRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Optional;

import static com.ureca.snac.common.BaseCode.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final JWTUtil jwtUtil;
    private final AuthRepository authRepository;
    private final SocialLinkRepository socialLinkRepository;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("loadUser 메소드 시작");
        String accessToken = userRequest.getAccessToken().getTokenValue();
        log.debug("OAuth2 accessToken={}", accessToken);

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.debug("OAuth2 사용자 정보={}", oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialProvider provider = SocialProvider.fromValue(registrationId);
        log.info("registrationId: {}", registrationId);

        OAuth2Response oAuth2Response = switch (provider) {
            case NAVER  -> new NaverResponse(oAuth2User.getAttributes());
            case GOOGLE -> new GoogleResponse(oAuth2User.getAttributes());
            case KAKAO  -> new KakaoResponse(oAuth2User.getAttributes());
        };

        String providerId = oAuth2Response.getProviderId();
        log.info("provider: {}, providerId: {}", provider, providerId);

        // state 파라미터가 JWT로 디코딩되는지 시도해서 플로우 구분
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String state = request.getParameter("state");

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
        stringRedisTemplate.opsForValue().set(redisKey, accessToken, Duration.ofMinutes(1));
        log.info("소셜 계정에서 내려준 AccessToken Redis 저장: {} = {}", redisKey, accessToken);


        if (isLinking) {
            // 소셜 연동
            // 이미 다른 계정에 해당 providerId가 연결되어 있는지 체크
            Optional<SocialLink> alreadyLinked =
                    socialLinkRepository.findByProviderAndProviderId(provider, providerId);
            if (alreadyLinked.isPresent()) {
                Member linkedMember = alreadyLinked.get().getMember();
                if (linkedMember.getEmail().equals(emailFromState)) {
                    log.info("같은 계정에 이미 연동된 소셜 계정입니다. provider={}, id={}", provider, providerId);
                    return new CustomOAuth2User(linkedMember, registrationId, providerId, oAuth2User.getAttributes());
                } else {
                    log.warn("이미 다른 계정에 연동된 소셜 계정: provider={}, id={}", provider, providerId);
                    throw new OAuth2AuthenticationException(
                            new OAuth2Error(OAUTH_DB_ALREADY_LINKED.getCode()),
                            "이미 다른 계정에 연동된 소셜 계정입니다.");
                }
            }

            // 연동 대상 회원 조회 및 ID 업데이트
            Optional<Member> email = authRepository.findByEmail(emailFromState);
            if (email.isEmpty()) {
                log.error("존재하지 않는 회원 이메일: {}", emailFromState);
                throw new OAuth2AuthenticationException(
                        new OAuth2Error(MEMBER_NOT_FOUND.getCode()),
                        "해당 회원을 찾을 수 없습니다.");
            }
            Member member = email.get();

            member.addSocialLink(provider, providerId);
            authRepository.save(member);
            log.info("social 연동 완료: {} -> {}", member.getEmail(), provider);

            return new CustomOAuth2User(member, registrationId, providerId, oAuth2User.getAttributes());
        }

        // -----------------------------------소셜 로그인 ---------------------------------------
        SocialLink socialLink = socialLinkRepository
                .findByProviderAndProviderId(provider, providerId)
                .orElseThrow(() -> {
                    log.warn("연동된 소셜 계정이 아님: provider={}, id={}", provider, providerId);
                    return new OAuth2AuthenticationException(
                            new OAuth2Error(OAUTH_DB_ACCOUNT_NOT_FOUND.getCode()),
                            "소셜 계정에 연동된 계정이 없습니다. 회원가입을 먼저 진행해주세요.");
                });

        Member existingMember = socialLink.getMember();
        log.info("기존 회원 로그인: email={}", existingMember.getEmail());
        return new CustomOAuth2User(existingMember, provider.getValue(), providerId, oAuth2User.getAttributes()
        );
    }
}
