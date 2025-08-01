package com.ureca.snac.auth.dto.request;

import com.ureca.snac.common.validation.KoreanPhone;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PhoneRequest {

    @NotBlank(message = "전화번호를 입력해주세요.")
    @KoreanPhone
    private String phone;
}