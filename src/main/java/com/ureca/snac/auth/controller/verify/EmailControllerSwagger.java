package com.ureca.snac.auth.controller.verify;

import com.ureca.snac.auth.dto.request.EmailRequest;
import com.ureca.snac.auth.dto.request.VerificationEmailRequest;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode409;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "인증", description = "이메일 인증 관련 API")
@RequestMapping("/api/email")
public interface EmailControllerSwagger {

    @Operation(summary = "이메일 인증 코드 발송", description = "회원가입을 위해 이메일로 인증 코드를 발송합니다.")
    @ApiSuccessResponse(description = "인증 코드 발송 성공")
    @ErrorCode409(description = "이미 가입된 이메일")
    @PostMapping("/send-verification-code")
    ResponseEntity<ApiResponse<Void>> sendVerificationCode(@Valid @RequestBody EmailRequest dto);

    @Operation(summary = "이메일 인증 코드 확인", description = "이메일로 발송된 인증 코드를 확인합니다.")
    @ApiSuccessResponse(description = "인증 성공")
    @ErrorCode400(description = "인증 코드가 일치하지 않음")
    @PostMapping("/verify-code")
    ResponseEntity<ApiResponse<Void>> verifyCode(@Valid @RequestBody VerificationEmailRequest dto);
}
