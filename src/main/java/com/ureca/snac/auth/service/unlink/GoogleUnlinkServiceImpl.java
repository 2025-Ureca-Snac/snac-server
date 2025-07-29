package com.ureca.snac.auth.service.unlink;

import com.ureca.snac.auth.exception.SocialUnlinkApiException;
import com.ureca.snac.auth.exception.SocialUnlinkException;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.Member;
import com.ureca.snac.auth.oauth2.SocialProvider;
import com.ureca.snac.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleUnlinkServiceImpl implements GoogleUnlinkService {

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
    @Transactional
    public void unlinkGoogleUser(String email) {
        Member member = authRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);

        String providerId = member.getGoogleId();
        log.info("providerId: {}", providerId);
        if (providerId == null) {
            throw new SocialUnlinkException(BaseCode.GOOGLE_NO_LINKED);
        }

        String redisKey = "google:" + providerId;
        String googleToken = stringRedisTemplate.opsForValue().get(redisKey);
        if (googleToken == null) {
            throw new SocialUnlinkException(BaseCode.GOOGLE_TOKEN_NOT_FOUND);
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
            throw new SocialUnlinkApiException(BaseCode.GOOGLE_UNLINK_FAILED, "구글 API 호출 실패: " + e.getMessage());
        }

        member.updateSocialId(SocialProvider.GOOGLE, null);
        stringRedisTemplate.delete(redisKey);
        log.info("구글 연동 해제 완료: {}", email);
    }
}
