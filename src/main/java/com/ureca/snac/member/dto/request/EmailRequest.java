package com.ureca.snac.member.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    @NotBlank
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;
}

