package com.ureca.snac.auth.controller.unlink;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.auth.dto.response.NaverUnlinkResponse;
import com.ureca.snac.auth.service.unlink.GoogleUnlinkService;
import com.ureca.snac.auth.service.unlink.KakaoUnlinkService;
import com.ureca.snac.auth.service.unlink.NaverUnlinkService;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.swagger.annotation.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SocialUnlinkController implements SocialUnlinkControllerSwagger {

    private final GoogleUnlinkService googleUnlinkService;
    private final KakaoUnlinkService kakaoUnlinkService;
    private final NaverUnlinkService naverUnlinkService;

    @Override
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> unlinkGoogle(
            @UserInfo CustomUserDetails userDetails
    ) {
        googleUnlinkService.unlinkGoogleUser(userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.of(
                        BaseCode.GOOGLE_UNLINK_SUCCESS,
                        Collections.singletonMap("revoked", true)
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String, Long>>> unlinkKakao(
            @UserInfo CustomUserDetails userDetails
    ) {
        Long unlinkedUserId = kakaoUnlinkService.unlinkKakaoUser(userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.of(
                        BaseCode.KAKAO_UNLINK_SUCCESS,
                        Collections.singletonMap("unlinked_user_id", unlinkedUserId)
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponse<NaverUnlinkResponse>> unlinkNaver(
            @UserInfo CustomUserDetails userDetails
    ) {
        NaverUnlinkResponse response = naverUnlinkService.unlinkNaverUser(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.of(BaseCode.NAVER_UNLINK_SUCCESS, response));
    }
}