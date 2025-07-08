package com.ureca.snac.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BaseCode {

    // common
    STATUS_OK("STATUS_OK_200", HttpStatus.OK, "서버가 정상적으로 동작 중입니다."),
    INVALID_INPUT("INVALID_INPUT_400", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),


    // join
    USER_SIGNUP_SUCCESS("USER_SIGNUP_SUCCESS_201", HttpStatus.CREATED, "정상적으로 회원가입 되었습니다."),
    EMAIL_DUPLICATE("EMAIL_DUPLICATE_409", HttpStatus.CONFLICT, "이미 사용중인 이메일입니다."),

    // 은행 - 성공
    BANK_CREATE_SUCCESS("BANK_CREATE_SUCCESS_201", HttpStatus.CREATED, "은행이 성공적으로 생성되었습니다."),
    BANK_READ_SUCCESS("BANK_READ_SUCCESS_200", HttpStatus.OK, "은행 정보를 성공적으로 조회했습니다."),
    BANK_LIST_SUCCESS("BANK_LIST_SUCCESS_200", HttpStatus.OK, "은행 목록을 성공적으로 조회했습니다."),
    BANK_UPDATE_SUCCESS("BANK_UPDATE_SUCCESS_200", HttpStatus.OK, "은행 정보가 성공적으로 수정되었습니다."),
    BANK_DELETE_SUCCESS("BANK_DELETE_SUCCESS_200", HttpStatus.OK, "은행이 성공적으로 삭제되었습니다."),

    // 은행 - 예외
    BANK_NOT_FOUND("BANK_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 은행을 찾을 수 없습니다.");



    private final String code;
    private final HttpStatus status;
    private final String message;
}
