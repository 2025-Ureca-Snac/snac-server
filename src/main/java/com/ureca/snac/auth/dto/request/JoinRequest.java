package com.ureca.snac.auth.dto.request;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JoinRequest {
    private String email;
    private String password;
    private String name;
    private String phone;
}
