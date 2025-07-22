package com.ureca.snac.member.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneChangeRequest {
    private String pwd;
    private String newPhone;
}
