package com.ureca.snac.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerificationPhoneRequest {
    private String phone;
    private String code;
}