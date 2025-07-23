package com.ureca.snac.auth.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.auth.service.KakaoService;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SocialUnlinkController implements SocialUnlinkControllerSwagger {

    private final KakaoService kakaoService;
    private final AuthRepository authRepository;


    @Override
    public ResponseEntity<ApiResponse<Map<String, Long>>> unlinkKakaoUser(CustomUserDetails customUserDetails) {

        String username = customUserDetails.getUsername();
        Member member = authRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);
        String kakaoId = member.getKakaoId();

        if (kakaoId == null) {
            return ResponseEntity.ok(ApiResponse.error(BaseCode.KAKAO_NO_LINKED));
        }

        try {
            Long unlinkedUserId = kakaoService.unlinkKakaoUser(Long.parseLong(kakaoId));
            if (unlinkedUserId != null) {
                return ResponseEntity.ok(ApiResponse.of(BaseCode.KAKAO_UNLINK_SUCCESS,
                        Collections.singletonMap("unlinked_user_id", unlinkedUserId)));
            } else {
                return ResponseEntity.ok(ApiResponse.error(BaseCode.KAKAO_API_ERROR));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error(BaseCode.KAKAO_UNLINK_FAILED));
        }
    }
}
