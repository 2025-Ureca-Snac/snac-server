package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.response.KakaoUnlinkResponse;
import com.ureca.snac.auth.exception.KakaoRequestException;
import com.ureca.snac.common.BaseCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class KakaoServiceImpl implements KakaoService {

    private final RestClient restClient;

    @Value("${kakao.admin-key}")
    private String kakaoAdminKey;

    private static final String KAKAO_API_BASE_URL = "https://kapi.kakao.com";

    public KakaoServiceImpl(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl(KAKAO_API_BASE_URL)
                .build();
    }

    @Override
    public Long unlinkKakaoUser(Long targetId) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("target_id_type", "user_id");
        body.add("target_id", String.valueOf(targetId));

        try {
            KakaoUnlinkResponse response = restClient.post()
                    .uri("/v1/user/unlink")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .header("Authorization", "KakaoAK " + kakaoAdminKey)
                    .body(body)
                    .retrieve()
                    .body(KakaoUnlinkResponse.class);

            if (response == null || response.getId() == null) {
                throw new KakaoRequestException(BaseCode.KAKAO_API_ERROR);
            }

            return response.getId();

        } catch (RestClientException e) {
            throw new KakaoRequestException(BaseCode.KAKAO_UNLINK_FAILED);
        }
    }
}
