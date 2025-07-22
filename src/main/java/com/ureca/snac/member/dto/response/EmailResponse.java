package com.ureca.snac.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EmailResponse {

    private String email;

    public static EmailResponse of(String email) {
        return new EmailResponse(email);
    }
}
