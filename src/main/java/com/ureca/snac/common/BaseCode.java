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

    // 머니 충전
    ORDER_NOT_FOUND("ORDER_NOT_FOUND_404", HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다"),
    AMOUNT_MISMATCH("AMOUNT_MISMATCH_400", HttpStatus.BAD_REQUEST, "주문 금액이 일치하지 않습니다"),
    ALREADY_PROCESSED_ORDER("ALREADY_PROCESSED_ORDER_400", HttpStatus.BAD_REQUEST, "이미 처리된 주문입니다"),
    // 머니 충전 - 성공
    MONEY_RECHARGE_PREPARE_SUCCESS("MONEY_RECHARGE_PREPARE_SUCCESS_200", HttpStatus.OK, "머니 충전 요청에 성공했습니다");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
