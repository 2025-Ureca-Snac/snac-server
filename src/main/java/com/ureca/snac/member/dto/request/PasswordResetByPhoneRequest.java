package com.ureca.snac.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetByPhoneRequest {
    private String phone;
    private String newPwd;
}
