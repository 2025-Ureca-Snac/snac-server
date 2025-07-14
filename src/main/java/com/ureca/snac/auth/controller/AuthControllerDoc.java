package com.ureca.snac.auth.controller;

import com.ureca.snac.auth.dto.request.LoginRequest;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증", description = "로그인·로그아웃 관련 API")
@RestController
@RequestMapping("/api")
public class AuthControllerDoc {
// Swagger 용 가짜
    @Operation(
            summary = "로그인",
            description = "이메일과 비밀번호로 로그인합니다. Access-Token은 응답 헤더에, Refresh-Token은 쿠키에 담아 반환합니다."
    )
    @ApiSuccessResponse(description = "로그인 성공")
    @ErrorCode401(description = "이메일 또는 비밀번호 불일치")
    @PostMapping(
            path = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "로그인 정보",
                    required = true
            )
            @RequestBody LoginRequest dto
    ) {
        return null;
    }

    @Operation(
            summary = "로그아웃",
            description = "쿠키에 저장된 Refresh-Token을 삭제하고 로그아웃 처리합니다."
    )
    @ApiSuccessResponse(description = "로그아웃 성공")
    @ErrorCode401(description = "Refresh-Token이 없거나 만료됨")
    @PostMapping(
            path = "/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ApiResponse<Void>> logout(
            @Parameter(
                    in = ParameterIn.COOKIE,
                    name = "refresh",
                    description = "삭제할 Refresh-Token 쿠키",
                    required = true
            )
            @CookieValue(name = "refresh", required = true) String refreshToken
    ) {
        return null;
    }
}
