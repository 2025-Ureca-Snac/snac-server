package com.ureca.snac.auth.controller;

import com.ureca.snac.auth.dto.request.JoinRequest;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode409;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "인증", description = "회원가입 관련 API")
@RequestMapping("/api")
public interface JoinControllerSwagger {

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름, 핸드폰 번호 등 사용자 정보를 받아 회원가입을 처리합니다.")
    @ApiSuccessResponse(description = "회원가입 성공")
    @ErrorCode400(description = "잘못된 요청 (유효성 검사 실패)")
    @ErrorCode409(description = "이메일 중복")
    @PostMapping("/join")
    ResponseEntity<ApiResponse<Void>> joinProcess(@RequestBody JoinRequest joinRequest);
}
