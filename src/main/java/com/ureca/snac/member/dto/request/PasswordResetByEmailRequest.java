package com.ureca.snac.member.dto.request;

import com.ureca.snac.common.validation.PasswordConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetByEmailRequest {
    private String email;

    @NotBlank(message = "새 비밀번호가 비어 있습니다.")
    @PasswordConstraint
    private String newPwd;
}
