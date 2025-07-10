package com.ureca.snac.auth.controller;

import com.ureca.snac.auth.dto.request.PhoneRequest;
import com.ureca.snac.auth.dto.response.VerificationCodeResponse;
import com.ureca.snac.auth.service.SnsServiceImpl;
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
    public ResponseEntity<ApiResponse<VerificationCodeResponse>> sendVerificationCode(@RequestBody PhoneRequest dto) {
        String code = snsService.sendVerificationCode(dto.getPhone());
        VerificationCodeResponse response = new VerificationCodeResponse(code);
        return ResponseEntity.ok(ApiResponse.of(SMS_VERIFICATION_SENT, response));
    }
}
