package com.ureca.snac.auth.controller.unlink;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.auth.dto.response.NaverUnlinkResponse;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.UserInfo;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Tag(name = "소셜 연동 해제", description = "연결된 소셜 계정(카카오, 네이버, 구글)의 연동을 해제합니다.")
@RequestMapping("/api/unlink")
@SecurityRequirement(name = "Authorization")
public interface SocialUnlinkControllerSwagger {

    @Operation(summary = "Google 계정 연동 해제", description = "현재 로그인된 사용자의 Google 소셜 연동을 해제합니다.")
    @ApiSuccessResponse(description = "Google 연동 해제 성공")
    @ErrorCode401
    @ErrorCode404(description = "연동된 Google 계정을 찾을 수 없습니다.")
    @PostMapping("/google")
    ResponseEntity<ApiResponse<Map<String, Boolean>>> unlinkGoogle(@UserInfo CustomUserDetails userDetails);

    @Operation(summary = "Kakao 계정 연동 해제", description = "현재 로그인된 사용자의 Kakao 소셜 연동을 해제합니다.")
    @ApiSuccessResponse(description = "Kakao 연동 해제 성공")
    @ErrorCode401
    @ErrorCode404(description = "연동된 Kakao 계정을 찾을 수 없습니다.")
    @PostMapping("/kakao")
    ResponseEntity<ApiResponse<Map<String, Long>>> unlinkKakao(@UserInfo CustomUserDetails userDetails);

    @Operation(summary = "Naver 계정 연동 해제", description = "현재 로그인된 사용자의 Naver 소셜 연동을 해제합니다.")
    @ApiSuccessResponse(description = "Naver 연동 해제 성공")
    @ErrorCode401
    @ErrorCode404(description = "연동된 Naver 계정을 찾을 수 없습니다.")
    @PostMapping("/naver")
    ResponseEntity<ApiResponse<NaverUnlinkResponse>> unlinkNaver(@UserInfo CustomUserDetails userDetails);
}
