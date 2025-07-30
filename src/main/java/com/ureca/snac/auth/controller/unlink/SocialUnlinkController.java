package com.ureca.snac.auth.controller.unlink;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.auth.dto.response.NaverUnlinkResponse;
import com.ureca.snac.auth.oauth2.SocialProvider;
import com.ureca.snac.auth.service.unlink.SocialUnlinkManager;
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

    private final SocialUnlinkManager socialUnlinkManager;

    @Override
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> unlinkGoogle(
            @UserInfo CustomUserDetails userDetails
    ) {
        Boolean revoked = socialUnlinkManager.unlink(SocialProvider.GOOGLE, userDetails.getUsername());
        return ResponseEntity.ok(
                ApiResponse.of(
                        BaseCode.GOOGLE_UNLINK_SUCCESS,
                        Collections.singletonMap("revoked", revoked)
                )
        );
    }

    @Override
    public ResponseEntity<ApiResponse<Map<String, Long>>> unlinkKakao(
            @UserInfo CustomUserDetails userDetails
    ) {
        Long unlinkedUserId = socialUnlinkManager.unlink(SocialProvider.KAKAO, userDetails.getUsername());
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
        NaverUnlinkResponse response = socialUnlinkManager.unlink(SocialProvider.NAVER, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.of(BaseCode.NAVER_UNLINK_SUCCESS, response));
    }
}