package com.ureca.snac.auth.service.unlink;

import com.ureca.snac.auth.dto.response.NaverUnlinkResponse;
import com.ureca.snac.auth.exception.SocialUnlinkApiException;
import com.ureca.snac.auth.exception.SocialUnlinkException;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.auth.oauth2.SocialProvider;
import com.ureca.snac.member.entity.SocialLink;
import com.ureca.snac.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverUnlinkServiceImpl implements SocialUnlinkService<NaverUnlinkResponse> {

    private final AuthRepository authRepository;
    private final RestClient restClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String clientSecret;

    @Autowired
    public NaverUnlinkServiceImpl(RestClient.Builder restClientBuilder,
                                  AuthRepository authRepository,
                                  StringRedisTemplate stringRedisTemplate) {
        this.restClient = restClientBuilder
                .baseUrl("https://nid.naver.com")
                .build();
        this.authRepository = authRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.NAVER;
    }

    @Override
    @Transactional
    public NaverUnlinkResponse unlink(String email) {
        Member member = authRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        Optional<SocialLink> socialLink = member.getSocialLink(getProvider());
        if (socialLink.isEmpty()) {
            throw new SocialUnlinkException(BaseCode.NAVER_NO_LINKED);
        }
        String providerId = socialLink.get().getProviderId();

        String redisKey = "NAVER:" + providerId;
        String naverToken = stringRedisTemplate.opsForValue().get(redisKey);
        if (naverToken == null) {
            throw new SocialUnlinkException(BaseCode.NAVER_TOKEN_NOT_FOUND);
        }

        try {
            NaverUnlinkResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/oauth2.0/token")
                            .queryParam("grant_type", "delete")
                            .queryParam("client_id", clientId)
                            .queryParam("client_secret", clientSecret)
                            .queryParam("access_token", naverToken)
                            .queryParam("service_provider", "NAVER")
                            .build())
                    .retrieve()
                    .body(NaverUnlinkResponse.class);

            member.removeSocialLink(getProvider());
            stringRedisTemplate.delete(redisKey);
            log.info("네이버 연동 해제 완료: {}", email);

            return response;
        } catch (RestClientException e) {
            throw new SocialUnlinkApiException(BaseCode.NAVER_API_CALL_ERROR, e.getMessage());
        }
    }
}
