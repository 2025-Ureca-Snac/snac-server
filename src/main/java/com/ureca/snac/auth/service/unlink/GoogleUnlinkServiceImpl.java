package com.ureca.snac.auth.service.unlink;

import com.ureca.snac.auth.exception.SocialUnlinkApiException;
import com.ureca.snac.auth.exception.SocialUnlinkFailedException;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.auth.oauth2.SocialProvider;
import com.ureca.snac.member.entity.SocialLink;
import com.ureca.snac.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleUnlinkServiceImpl implements SocialUnlinkService<Void> {

    private final AuthRepository authRepository;
    private final RestClient restClient;
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public GoogleUnlinkServiceImpl(RestClient.Builder restClientBuilder,
                                   AuthRepository authRepository,
                                   StringRedisTemplate stringRedisTemplate) {
        this.restClient = restClientBuilder
                .baseUrl("https://oauth2.googleapis.com")
                .build();
        this.authRepository = authRepository;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.GOOGLE;
    }

    @Override
    @Transactional
    public Void unlink(String email) {
        Member member = authRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        Optional<SocialLink> socialLink = member.getSocialLink(getProvider());
        if (socialLink.isEmpty()) {
            throw new SocialUnlinkFailedException(BaseCode.GOOGLE_NO_LINKED);
        }

        String providerId = socialLink.get().getProviderId();
        log.info("providerId: {}", providerId);

        String redisKey = "GOOGLE:" + providerId;
        String googleToken = stringRedisTemplate.opsForValue().get(redisKey);
        if (googleToken == null) {
            throw new SocialUnlinkFailedException(BaseCode.GOOGLE_TOKEN_NOT_FOUND);
        }

        try {
            restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/revoke")
                            .queryParam("token", googleToken)
                            .build())
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
/*            if (e.getMessage().contains("invalid_token")) {
                log.warn("구글 revoke: invalid_token 무시, 내부 unlink만 수행");
                //“invalid_token: Token is not revocable.” 오류는 해당 토큰이 이미 만료되었거나, 액세스 토큰(access token)이어서 리보크 대상이 아닌 경우에 발생.
                // Google 공식 가이드와 커뮤니티 권고에 따르면, 이 오류는 “토큰이 이미 폐기되었음”을 의미하므로 무시하고 내부 unlink 처리만 수행해도 무방
            } else */
                throw new SocialUnlinkApiException(BaseCode.GOOGLE_UNLINK_FAILED, "구글 API 호출 실패: " + e.getMessage());

        }

        member.removeSocialLink(getProvider());
        stringRedisTemplate.delete(redisKey);
        log.info("구글 연동 해제 완료: {}", email);
        return null;
    }
}
