package com.ureca.snac.member.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.member.dto.request.*;
import com.ureca.snac.swagger.annotation.UserInfo;
import com.ureca.snac.swagger.annotation.error.*;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "회원 정보", description = "회원 정보(비밀번호, 전화번호, 닉네임 등)를 관리합니다.")
@RequestMapping("/api/member")
public interface MemberControllerSwagger {

    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호를 확인하고 새로운 비밀번호로 변경합니다.")
    @SecurityRequirement(name = "Authorization")
    @ApiSuccessResponse(description = "비밀번호 변경 성공")
    @ErrorCode400(description = "잘못된 요청 파라미터입니다.")
    @ErrorCode401
    @ErrorCode403
    @ErrorCode500
    @PostMapping("/change-pwd")
    ResponseEntity<ApiResponse<Void>> changePwd(
            @UserInfo CustomUserDetails userDetails,
            @Valid @RequestBody PasswordChangeRequest request
    );

    @Operation(summary = "전화번호 변경 전 비밀번호 확인", description = "전화번호 변경을 위해 현재 비밀번호를 확인하고, 새로운 전화번호로 인증번호를 전송합니다.")
    @SecurityRequirement(name = "Authorization")
    @ApiSuccessResponse(description = "비밀번호 확인 및 인증번호 전송 성공")
    @ErrorCode400(description = "잘못된 요청 파라미터입니다.")
    @ErrorCode401
    @ErrorCode403
    @ErrorCode500
    @PostMapping("/change-phone/check")
    ResponseEntity<ApiResponse<Void>> checkPhone(
            @UserInfo CustomUserDetails userDetails,
            @Valid @RequestBody PhoneChangeRequest phoneChangeRequest);

    @Operation(summary = "전화번호 변경", description = "인증된 전화번호로 변경합니다.")
    @SecurityRequirement(name = "Authorization")
    @ApiSuccessResponse(description = "전화번호 변경 성공")
    @ErrorCode400(description = "인증되지 않은 전화번호입니다.")
    @ErrorCode401
    @ErrorCode403
    @ErrorCode500
    @PostMapping("/change-phone")
    ResponseEntity<ApiResponse<Void>> changePhone(
            @UserInfo CustomUserDetails userDetails,
            @Valid @RequestBody PhoneRequest phoneRequest
    );

    @Operation(summary = "닉네임 중복 확인", description = "입력한 닉네임이 사용 가능한지 확인합니다.")
    @ApiSuccessResponse(description = "사용 가능한 닉네임입니다.")
    @ErrorCode400(description = "요청 파라미터가 유효하지 않습니다.")
    @ErrorCode409(description = "이미 사용 중인 닉네임입니다.")
    @ErrorCode500
    @PostMapping("/check-nickname")
    ResponseEntity<ApiResponse<Void>> checkNicknameDuplicate(@Valid @RequestBody NicknameCheckRequest request);


    @Operation(summary = "닉네임 변경", description = "닉네임을 변경합니다. 마지막 변경일로부터 1일이 지나야 변경 가능합니다.")
    @SecurityRequirement(name = "Authorization")
    @ApiSuccessResponse(description = "닉네임 변경 성공, 응답 데이터는 마지막 변경일입니다.")
    @ErrorCode400(description = "잘못된 요청 파라미터 또는 1일 이내에 변경 이력이 존재합니다.")
    @ErrorCode401
    @ErrorCode403
    @ErrorCode500
    @PostMapping("/change-nickname")
    ResponseEntity<ApiResponse<String>> changeNickname(
            @UserInfo CustomUserDetails userDetails,
            @Valid @RequestBody NicknameChangeRequest nicknameChangeRequest);

    @Operation(summary = "이메일 찾기", description = "휴대폰 인증을 통해 가입된 이메일을 찾습니다.")
    @ApiSuccessResponse(description = "이메일 찾기 성공")
    @ErrorCode400(description = "인증되지 않은 전화번호입니다.")
    @ErrorCode404(description = "해당 전화번호로 가입된 회원이 없습니다.")
    @ErrorCode500
    @PostMapping("/find-email")
    ResponseEntity<ApiResponse<String>> findEmail(@Valid @RequestBody PhoneRequest phoneRequest);

    @Operation(summary = "휴대폰 인증으로 비밀번호 재설정", description = "휴대폰 인증 후 비밀번호를 재설정합니다.")
    @ApiSuccessResponse(description = "비밀번호 재설정 성공")
    @ErrorCode400(description = "인증되지 않은 전화번호입니다.")
    @ErrorCode404(description = "해당 전화번호로 가입된 회원이 없습니다.")
    @ErrorCode500
    @PostMapping("/find-pwd/phone")
    ResponseEntity<ApiResponse<Void>> resetPwdByPhone(
            @Valid @RequestBody PasswordResetByPhoneRequest req);

    @Operation(summary = "이메일 인증으로 비밀번호 재설정", description = "이메일 인증 후 비밀번호를 재설정합니다.")
    @ApiSuccessResponse(description = "비밀번호 재설정 성공")
    @ErrorCode400(description = "인증되지 않은 이메일입니다.")
    @ErrorCode404(description = "해당 이메일로 가입된 회원이 없습니다.")
    @ErrorCode500
    @PostMapping("/find-pwd/email")
    ResponseEntity<ApiResponse<Void>> resetPwdByEmail(
            @Valid @RequestBody PasswordResetByEmailRequest req);
}
