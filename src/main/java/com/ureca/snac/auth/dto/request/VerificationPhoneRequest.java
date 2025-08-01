package com.ureca.snac.auth.dto.request;

import com.ureca.snac.common.validation.KoreanPhone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerificationPhoneRequest {

    @NotBlank(message = "전화번호를 입력해주세요.")
    @KoreanPhone
    private String phone;

    @NotBlank(message = "인증 코드를 입력해주세요.")
    @Pattern(regexp = "^\\d{6}$", message = "인증 코드는 숫자 6자리 숫자여야 합니다.")
    private String code;
}