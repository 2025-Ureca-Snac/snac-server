package com.ureca.snac.member.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.auth.service.SnsService;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.dto.request.*;
import com.ureca.snac.member.dto.response.EmailResponse;
import com.ureca.snac.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final SnsService snsService;

    //TODO 비밀번호 변경
    // 사용자 인증 추가 해야 할듯? -> 이렇게 말고, 로그아웃 시켜버리는 방식으로
    @PostMapping("/change-pwd")
    public ResponseEntity<ApiResponse<Void>> changePwd(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PasswordChangeRequest request
    ) {
        memberService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.PASSWORD_CHANGED));
    }


    //TODO 전화번호 변경

    // 1. 비밀번호 맞는지 확인 하고, 인증번호 요청
    @GetMapping("/change-phone")
    public ResponseEntity<ApiResponse<Void>> checkPhone(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PhoneChangeRequest phoneChangeRequest) {
        memberService.checkPassword(userDetails.getUsername(), phoneChangeRequest);
        snsService.sendVerificationCode(phoneChangeRequest.getNewPhone());
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.PHONE_EXIST_SUCCESS));
    }

    // 2. 인증 번호 맞는지 확인

    // 3. 맞으면 교체
    @PostMapping("/change-phone")
    public ResponseEntity<ApiResponse<Void>> changePhone(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PhoneRequest phoneRequest
            ) {
        String changePhone = phoneRequest.getPhone();
        snsService.isPhoneVerified(changePhone);
        memberService.changePhone(userDetails.getUsername(), changePhone);
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.PHONE_CHANGED));
    }

    //TODO
    // 닉네임 변경
    @PostMapping("/change-nickname")
    public ResponseEntity<ApiResponse<String>> changeNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody NicknameChangeRequest nicknameChangeRequest) {
        String lastUpdated = memberService.changeNickname(userDetails.getUsername(), nicknameChangeRequest);
        return ResponseEntity.ok(ApiResponse.of(BaseCode.NICKNAME_CHANGED, lastUpdated));
    }

    //TODO 휴대폰 인증으로 이메일 찾기 (완료)

    // 1. 이메일 찾기 시도하면 휴대폰 인증 시키기

    // 2. 검증

    // 3. 검증 되었는지 확인 후 이메일 찾아주기
    @PostMapping("/find-email")
    public ResponseEntity<ApiResponse<EmailResponse>> findEmail(@RequestBody PhoneRequest phoneRequest) {
        String phone = phoneRequest.getPhone();
        snsService.isPhoneVerified(phone);
        EmailResponse emailResponse = memberService.findEmailByPhone(phone);

        return ResponseEntity.ok(ApiResponse.of(BaseCode.EMAIL_FOUND_BY_PHONE, emailResponse));
    }


    //TODO
    // 비밀번호 찾기 (휴대폰 번호 또는 이메일)


    //TODO
    // 마이페이지 가져오기

}

