package com.ureca.snac.auth.controller.verify;

import com.ureca.snac.auth.dto.request.PhoneRequest;
import com.ureca.snac.auth.dto.request.VerificationPhoneRequest;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "인증", description = "SMS 인증 관련 API")
@RequestMapping("/api/sns")
public interface SnsControllerSwagger {

    @Operation(summary = "SMS 인증 코드 발송", description = "회원가입을 위해 휴대폰으로 인증 코드를 발송합니다.")
    @ApiSuccessResponse(description = "인증 코드 발송 성공")
    @PostMapping("/send-verification-code")
    ResponseEntity<ApiResponse<Void>> sendVerificationCode(@RequestBody PhoneRequest dto);

    @Operation(summary = "SMS 인증 코드 확인", description = "휴대폰으로 발송된 인증 코드를 확인합니다.")
    @ApiSuccessResponse(description = "인증 성공")
    @ErrorCode400(description = "인증 코드가 일치하지 않음")
    @PostMapping("/verify-code")
    ResponseEntity<ApiResponse<Void>> verifyCode(@RequestBody VerificationPhoneRequest dto);
}
