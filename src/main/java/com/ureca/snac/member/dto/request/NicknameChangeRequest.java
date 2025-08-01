package com.ureca.snac.member.dto.request;

import com.ureca.snac.common.validation.NicknameConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor


public class NicknameChangeRequest {

    @NotBlank(message = "닉네임은 비어 있을 수 없습니다.")
    @NicknameConstraint
    private String nickname;
}
