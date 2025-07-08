package com.ureca.snac.auth.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JoinDto {
    private String email;
    private String password;
    private String name;
    private String phone;
}
