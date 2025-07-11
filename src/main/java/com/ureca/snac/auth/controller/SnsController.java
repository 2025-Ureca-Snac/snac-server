package com.ureca.snac.auth.controller;

import com.ureca.snac.auth.dto.request.PhoneRequest;
import com.ureca.snac.auth.dto.request.VerificationRequest;
import com.ureca.snac.auth.service.SnsService;
import com.ureca.snac.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.*;

@RestController
@RequiredArgsConstructor
public class SnsController {

    private final SnsService snsService;

    @PostMapping("/api/send-verification-code")
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(@RequestBody PhoneRequest dto) {
        snsService.sendVerificationCode(dto.getPhone());
        return ResponseEntity.ok(ApiResponse.ok(SMS_VERIFICATION_SENT));
    }

    @PostMapping("/api/verify-code")
    public ResponseEntity<ApiResponse<Void>> verifyCode(@RequestBody VerificationRequest dto) {
        snsService.verifyCode(dto.getPhone(), dto.getCode());
        return ResponseEntity.ok(ApiResponse.ok(SMS_CODE_VERIFICATION_SUCCESS));
    }
}
