package com.ureca.snac.auth.controller;

import com.ureca.snac.auth.dto.request.EmailRequest;
import com.ureca.snac.auth.dto.request.VerificationEmailRequest;
import com.ureca.snac.auth.service.EmailService;
import com.ureca.snac.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-verification-code")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(@RequestBody EmailRequest dto) {
        emailService.sendVerificationCode(dto.getEmail());
        return ResponseEntity.ok(ApiResponse.ok(EMAIL_VERIFICATION_SENT));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<Void>> verifyCode(@RequestBody VerificationEmailRequest dto) {
        emailService.verifyCode(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok(ApiResponse.ok(EMAIL_CODE_VERIFICATION_SUCCESS));
    }
}