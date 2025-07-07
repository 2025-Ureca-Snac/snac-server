package com.ureca.snac.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BaseCode {
    STATUS_OK("STATUS_OK_200", HttpStatus.OK, "서버가 정상적으로 동작 중입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
