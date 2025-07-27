package com.ureca.snac.mypage;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.mypage.dto.MyPageResponse;
import com.ureca.snac.swagger.annotation.UserInfo;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "마이페이지", description = "마이페이지 관련 API")
@RequestMapping("/api")
public interface MyPageControllerSwagger {

    @GetMapping("/mypage")
    @Operation(summary = "마이페이지 첫 화면", description = "마이페이지의 첫 화면에 필요한 정보들을 반환합니다.")
    @SecurityRequirement(name = "Authorization")
    @ApiSuccessResponse(description = "마이페이지 정보 조회 성공")
    @ErrorCode401
    @ErrorCode404(description = "해당 회원을 찾을 수 없습니다.")
    ResponseEntity<ApiResponse<MyPageResponse>> getMyPageInfo(@UserInfo CustomUserDetails userDetails);
}
