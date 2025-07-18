package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.CustomUserDetails;
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

import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final JWTUtil jwtUtil;
    private final AuthRepository authRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("oAuth2User: {}", oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("kakao")) {

            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String provider = oAuth2Response.getProvider();
        String providerId = oAuth2Response.getProviderId();

        Optional<Member> existData;
        if (provider.equals("naver")) {
            existData = authRepository.findByNaverId(providerId);
        } else if (provider.equals("google")) {
            existData = authRepository.findByGoogleId(providerId);
        } else {
            existData = authRepository.findByKakaoId(providerId);
        }

        if (existData.isPresent()) {
            return new CustomUserDetails(existData.get());
        }

        // 사용자 정보 state 에서 꺼내서 확인
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String state = request.getParameter("state");

        String email = jwtUtil.getUsername(state);
        Member member = authRepository.findByEmail(email)
                .orElseThrow(() -> new OAuth2AuthenticationException("존재하지 않는 회원입니다. 신규로 소셜 로그인 계정을 등록하겠습니다."));

        member.updateSocialId(provider, providerId);
        authRepository.save(member);

        return new CustomUserDetails(member);
    }
}
