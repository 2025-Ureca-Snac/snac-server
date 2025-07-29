package com.ureca.snac.auth.service.unlink;

import com.ureca.snac.auth.dto.response.KakaoUnlinkResponse;
import com.ureca.snac.auth.exception.KakaoRequestException;
import com.ureca.snac.auth.exception.SocialUnlinkApiException;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.Member;
import com.ureca.snac.auth.oauth2.SocialProvider;
import com.ureca.snac.member.exception.MemberNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@Transactional
public class KakaoUnlinkServiceImpl implements KakaoUnlinkService {

    private final RestClient restClient;
    private final AuthRepository authRepository;

    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;

    public KakaoUnlinkServiceImpl(RestClient.Builder restClientBuilder, AuthRepository authRepository) {
        this.restClient = restClientBuilder
                .baseUrl("https://kapi.kakao.com")
                .build();
        this.authRepository = authRepository;
    }

    @Override
    public Long unlinkKakaoUser(String email) {

        Member member = authRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
        String kakaoId = member.getKakaoId();

        if (kakaoId == null) {
            throw new KakaoRequestException(BaseCode.KAKAO_NO_LINKED);
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", kakaoId);

        try {
            KakaoUnlinkResponse response = restClient.post()
                    .uri("/v1/user/unlink")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .header("Authorization", "KakaoAK " + kakaoAdminKey)
                    .body(body)
                    .retrieve()
                    .body(KakaoUnlinkResponse.class);

            if (response == null || response.id() == null) {
                throw new SocialUnlinkApiException(BaseCode.KAKAO_API_CALL_ERROR);
            }

            member.updateSocialId(SocialProvider.KAKAO, null);

            log.info("카카오 연동 해제 완료.");
            return response.id();

        } catch (RestClientException e) {
            throw new SocialUnlinkApiException(BaseCode.KAKAO_API_CALL_ERROR, e.getMessage());
        }
    }
}
