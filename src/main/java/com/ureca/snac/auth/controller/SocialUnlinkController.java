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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/unlink")
@RequiredArgsConstructor
public class SocialUnlinkController {

    private final KakaoService kakaoService;
    private final AuthRepository authRepository;


    @PostMapping("/kakao")
    public ResponseEntity<ApiResponse<Map<String, Long>>> unlinkUser(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        String username = customUserDetails.getUsername();
        Member member = authRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);
        String kakaoId = member.getKakaoId();

        if (kakaoId == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error(BaseCode.KAKAO_NO_LINKED));
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