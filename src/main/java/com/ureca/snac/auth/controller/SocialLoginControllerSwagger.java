package com.ureca.snac.auth.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "인증", description = "소셜 로그인 관련 API")
@RequestMapping("/api")
public interface SocialLoginControllerSwagger {
    @Operation(summary = "소셜 로그인", description = "소셜 로그인 후 받은 social 토큰으로 access, refresh 토큰을 발급합니다.")
    @Parameter(name = HttpHeaders.AUTHORIZATION, in = ParameterIn.HEADER, required = true, description = "Bearer [social_token]")
    @ApiSuccessResponse(description = "소셜 로그인 성공")
    @ErrorCode401(description = "유효하지 않은 소셜 토큰")
    @PostMapping("/social-login")
    ResponseEntity<ApiResponse<Void>> socialLogin(HttpServletRequest request, HttpServletResponse response);
}