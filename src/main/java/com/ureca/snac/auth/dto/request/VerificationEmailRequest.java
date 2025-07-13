package com.ureca.snac.auth.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerificationEmailRequest {
    private String email;
    private String code;
}