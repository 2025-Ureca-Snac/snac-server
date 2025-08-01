package com.ureca.snac.member.dto.request;

import com.ureca.snac.common.validation.DifferentPasswords;
import com.ureca.snac.common.validation.PasswordConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@DifferentPasswords
public class PasswordChangeRequest {
    @NotBlank(message = "현재 비밀번호가 비어있습니다.")
    private String currentPwd;

    @NotBlank(message = "새 비밀번호가 비어있습니다.")
    @PasswordConstraint
    private String newPwd;
}
