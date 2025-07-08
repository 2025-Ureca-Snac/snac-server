package com.ureca.snac.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BaseCode {
    // join
    STATUS_OK("STATUS_OK_200", HttpStatus.OK, "서버가 정상적으로 동작 중입니다."),
    USER_SIGNUP_SUCCESS("USER_SIGNUP_SUCCESS_201", HttpStatus.CREATED, "정상적으로 회원가입 되었습니다."),
    EMAIL_DUPLICATE("EMAIL_DUPLICATE_409", HttpStatus.CONFLICT, "이미 사용중인 이메일입니다.");


    private final String code;
    private final HttpStatus status;
    private final String message;
}
