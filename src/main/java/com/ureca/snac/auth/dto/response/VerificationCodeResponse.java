package com.ureca.snac.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VerificationCodeResponse {
    private String verificationCode;
}