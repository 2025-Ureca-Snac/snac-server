package com.ureca.snac.member.controller;

import com.ureca.snac.member.dto.request.PhoneRequest;
import com.ureca.snac.auth.dto.request.VerificationPhoneRequest;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.member.dto.response.EmailResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Member", description = "회원 관련 API")
public interface MemberControllerSwagger {

    @Operation(summary = "이메일 찾기 인증 코드 발송", description = "이메일 찾기를 위한 인증 코드를 휴대폰으로 발송합니다.")
    @ApiSuccessResponse(description = "인증 코드 발송 성공")
    @ErrorCode404(description = "해당 휴대폰 번호로 가입된 회원이 없음")
    @PostMapping("/find-email/code")
    ResponseEntity<ApiResponse<Void>> sendVerificationCodeForFindEmail(@RequestBody PhoneRequest dto);

    @Operation(summary = "이메일 찾기", description = "휴대폰 인증 후 이메일을 반환합니다.")
    @ApiSuccessResponse(description = "이메일 찾기 성공")
    @ErrorCode400(description = "인증 코드가 일치하지 않음")
    @ErrorCode404(description = "해당 휴대폰 번호로 가입된 회원이 없음")
    @PostMapping("/find-email")
    ResponseEntity<ApiResponse<EmailResponse>> findEmail(@RequestBody VerificationPhoneRequest dto);
}