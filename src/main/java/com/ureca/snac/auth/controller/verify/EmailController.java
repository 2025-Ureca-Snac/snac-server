package com.ureca.snac.auth.controller.verify;

import com.ureca.snac.auth.dto.request.EmailRequest;
import com.ureca.snac.auth.dto.request.VerificationEmailRequest;
import com.ureca.snac.auth.service.verify.EmailService;
import com.ureca.snac.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.*;

@RestController
@RequiredArgsConstructor
public class EmailController implements EmailControllerSwagger {

    private final EmailService emailService;

    @Override
    public ResponseEntity<ApiResponse<Void>> sendVerificationCode(@RequestBody EmailRequest dto) {
        emailService.sendVerificationCode(dto.getEmail());
        return ResponseEntity.ok(ApiResponse.ok(EMAIL_VERIFICATION_SENT));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> verifyCode(@RequestBody VerificationEmailRequest dto) {
        emailService.verifyCode(dto.getEmail(), dto.getCode());
        return ResponseEntity.ok(ApiResponse.ok(EMAIL_CODE_VERIFICATION_SUCCESS));
    }
}