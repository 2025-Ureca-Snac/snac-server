package com.ureca.snac.member.dto.request;

import com.ureca.snac.common.validation.KoreanPhone;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneChangeRequest {
    @NotBlank(message = "현재 비밀번호가 비어 있습니다.")
    private String pwd;

    @NotBlank(message = "새 전화번호가 비어 있습니다.")
    @KoreanPhone
    private String newPhone;
}
