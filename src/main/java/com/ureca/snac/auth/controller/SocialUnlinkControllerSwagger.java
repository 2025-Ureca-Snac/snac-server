package com.ureca.snac.auth.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.UserInfo;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode500;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Tag(name = "소셜 연동 해제", description = "소셜 연동 해제 관련 API")
@RequestMapping("/api/unlink")
public interface SocialUnlinkControllerSwagger {

    @Operation(summary = "카카오 연동 해제", description = "현재 로그인된 사용자의 카카오 소셜 연동을 해제합니다.")
    @SecurityRequirement(name = "Authorization")
    @ApiSuccessResponse(description = "카카오 연동 해제 성공")
    @ErrorCode400(description = "카카오 연동이 되어있지 않은 계정입니다.")
    @ErrorCode401
    @ErrorCode500(description = "카카오 연동 해제 중 서버 에러가 발생했습니다.")
    @PostMapping("/kakao")
    ResponseEntity<ApiResponse<Map<String, Long>>> unlinkKakaoUser(
            @UserInfo CustomUserDetails customUserDetails
    );
}
