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
    EMAIL_DUPLICATE("EMAIL_DUPLICATE_409", HttpStatus.CONFLICT, "이미 사용중인 이메일입니다."),

    // 은행 - 성공
    BANK_CREATE_SUCCESS("BANK_CREATE_SUCCESS_201", HttpStatus.CREATED, "은행이 성공적으로 생성되었습니다."),
    BANK_READ_SUCCESS("BANK_READ_SUCCESS_200", HttpStatus.OK, "은행 정보를 성공적으로 조회했습니다."),
    BANK_LIST_SUCCESS("BANK_LIST_SUCCESS_200", HttpStatus.OK, "은행 목록을 성공적으로 조회했습니다."),
    BANK_UPDATE_SUCCESS("BANK_UPDATE_SUCCESS_200", HttpStatus.OK, "은행 정보가 성공적으로 수정되었습니다."),
    BANK_DELETE_SUCCESS("BANK_DELETE_SUCCESS_200", HttpStatus.OK, "은행이 성공적으로 삭제되었습니다."),

    // 은행 - 예외
    BANK_NOT_FOUND("BANK_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 은행을 찾을 수 없습니다."),

    // 거래 관련
    TRADE_NOT_FOUND("TRADE_NOT_FOUND_404", HttpStatus.NOT_FOUND, "거래를 찾을 수 없습니다."),
    TRADE_STATUS_MISMATCH("TRADE_STATUS_MISMATCH_409", HttpStatus.CONFLICT, "현재 단계에서 수행할 수 없는 요청입니다."),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE_409", HttpStatus.CONFLICT, "잔액이 부족합니다."),
    DUPLICATE_TRADE_REQUEST("DUPLICATE_TRADE_REQUEST_409", HttpStatus.CONFLICT, "이미 요청된 거래가 있습니다."),
    TRADE_SELF_REQUEST("TRADE_SELF_REQUEST_400", HttpStatus.BAD_REQUEST, "자신의 글에는 거래를 요청할 수 없습니다."),
    CARD_NOT_FOUND("CARD_NOT_FOUND_404", HttpStatus.NOT_FOUND, "거래카드를 찾을 수 없습니다.");

    ;
    private final String code;
    private final HttpStatus status;
    private final String message;
}
