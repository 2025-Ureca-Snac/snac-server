package com.ureca.snac.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NicknameCheckRequest {

    @NotBlank(message = "닉네임은 비어 있을 수 없습니다.")
    private String nickname;
}
