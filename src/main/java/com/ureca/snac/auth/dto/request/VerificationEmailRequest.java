package com.ureca.snac.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerificationEmailRequest {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "인증 코드를 입력해주세요.")
    @Pattern(regexp = "^\\d{6}$", message = "인증 코드는 숫자 6자리 숫자여야 합니다.")
    private String code;
}