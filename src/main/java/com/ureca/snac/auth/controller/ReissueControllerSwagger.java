package com.ureca.snac.auth.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "인증", description = "토큰 재발급 API")
@RequestMapping("/api")
@SecurityRequirement(name = "Authorization")
public interface ReissueControllerSwagger {

    @Operation(summary = "Access Token 재발급", description = "HttpOnly 쿠키에 담긴 Refresh Token을 사용하여 새로운 Access Token과 Refresh Token을 발급받습니다.")
    @ApiSuccessResponse(description = "토큰 재발급 성공. 응답 헤더(Authorization)와 쿠키(refresh)에 새로운 토큰이 포함됩니다.")
    @ErrorCode400(description = "유효하지 않은 Refresh Token")
    @PostMapping("/reissue")
    ResponseEntity<ApiResponse<Void>> reissue(HttpServletRequest request, HttpServletResponse response);
}
