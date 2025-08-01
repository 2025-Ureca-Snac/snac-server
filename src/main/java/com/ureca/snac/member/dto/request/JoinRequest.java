package com.ureca.snac.member.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ureca.snac.common.validation.KoreanName;
import com.ureca.snac.common.validation.KoreanPhone;
import com.ureca.snac.common.validation.NicknameConstraint;
import com.ureca.snac.common.validation.PasswordConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JoinRequest {

    @Email
    @NotBlank(message = "이메일이 비어 있습니다.")
    private String email;

    @NotBlank(message = "비밀번호가 비어 있습니다.")
    @PasswordConstraint
    private String password;

    @NotBlank(message = "이름이 비어 있습니다.")
    @KoreanName
    private String name;

    @NotBlank(message = "휴대폰 번호가 비어 있습니다.")
    @KoreanPhone
    private String phone;

    @NicknameConstraint
    private String nickname;

    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate birthDate;
}
