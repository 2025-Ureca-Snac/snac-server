package com.ureca.snac.member.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.dto.request.EmailRequest;
import com.ureca.snac.member.dto.request.PasswordChangeRequest;
import com.ureca.snac.member.dto.request.PhoneRequest;
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

    @PostMapping("/find-email")
    public ResponseEntity<ApiResponse<EmailResponse>> findEmail(@RequestBody PhoneRequest phoneRequest) {
        EmailResponse emailResponse = memberService.findEmailByPhone(phoneRequest);

        return ResponseEntity.ok(ApiResponse.of(BaseCode.EMAIL_FOUND_BY_PHONE, emailResponse));
    }

    @GetMapping("/email-exist")
    public ResponseEntity<ApiResponse<Boolean>> emailExist(@RequestParam("email") String email) {
        Boolean exist = memberService.emailExist(new EmailRequest(email));
        return ResponseEntity.ok(ApiResponse.of(BaseCode.EMAIL_IS_EXIST, exist));
    }

/*    @PostMapping("/change-pwd")
    public ResponseEntity<ApiResponse<Void>> changePwd(@RequestBody ) {
        // 이메일 인증 or 문자 인증 되었는지 확인


        //확인 되었으면 변경

    }*/

    @PostMapping("/change-pwd")
    public ResponseEntity<ApiResponse<Void>> changePwd(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody PasswordChangeRequest request
    ) {
        memberService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.PASSWORD_CHANGED));
    }
}

