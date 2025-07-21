package com.ureca.snac.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class JoinRequest {
    private String email;
    private String password;
    private String name;
    private String phone;
    private String nickname;

    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate birthDate;
}
