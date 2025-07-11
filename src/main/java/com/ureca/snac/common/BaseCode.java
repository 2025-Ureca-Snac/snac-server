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

    // 회원가입 - 성공
    USER_SIGNUP_SUCCESS("USER_SIGNUP_SUCCESS_201", HttpStatus.CREATED, "정상적으로 회원가입 되었습니다."),

    // 회원가입 - 예외
    EMAIL_DUPLICATE("EMAIL_DUPLICATE_409", HttpStatus.CONFLICT, "이미 사용중인 이메일입니다."),

    // 로그인 시도 - 성공
    LOGIN_SUCCESS("LOGIN_SUCCESS_200", HttpStatus.OK, "로그인에 성공했습니다."),

    // 로그인 시도 - 실패
    LOGIN_FAILED("LOGIN_FAILED_401", HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),

    // 로그아웃 시도 - 성공
    LOGOUT_SUCCESS("LOGOUT_SUCCESS_200", HttpStatus.OK, "로그아웃에 성공했습니다."),


    // 인증,인가
    TOKEN_EXPIRED("TOKEN_EXPIRED_401", HttpStatus.UNAUTHORIZED, "엑세스 토큰이 만료되었습니다."),
    TOKEN_INVALID("TOKEN_INVALID_401", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    REISSUE_SUCCESS("REISSUE_SUCCESS_200", HttpStatus.OK, "액세스 토큰이 재발급되었습니다."),
    REFRESH_TOKEN_NULL("REFRESH_TOKEN_NULL_400", HttpStatus.BAD_REQUEST, "refresh 토큰이 없습니다."),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED_400", HttpStatus.BAD_REQUEST, "refresh 토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN_400", HttpStatus.BAD_REQUEST, "유효하지 않은 refresh 토큰입니다."),

    // 인증코드 발송- 성공
    SMS_VERIFICATION_SENT("SMS_VERIFICATION_SENT_200", HttpStatus.OK, "인증번호가 발송되었습니다."),
    // 인증코드 발송- 예외
    SMS_SEND_FAILED("SMS_SEND_FAILED_500", HttpStatus.INTERNAL_SERVER_ERROR, "SMS 전송에 실패했습니다."),

    //인증코드 인증- 성공
    SMS_CODE_VERIFICATION_SUCCESS("SMS_CODE_VERIFICATION_SUCCESS_200", HttpStatus.OK, "인증에 성공했습니다."),

    // 인증코드 인증- 예외
    SMS_CODE_VERIFICATION_EXPIRED("SMS_CODE_VERIFICATION_EXPIRED_401",HttpStatus.UNAUTHORIZED,"인증번호가 만료되었거나 존재하지 않습니다."),
    SMS_CODE_VERIFICATION_MISMATCH("SMS_CODE_VERIFICATION_MISMATCH_401",HttpStatus.UNAUTHORIZED, "인증번호가 일치하지 않습니다."),

    // 은행 - 성공
    BANK_CREATE_SUCCESS("BANK_CREATE_SUCCESS_201", HttpStatus.CREATED, "은행이 성공적으로 생성되었습니다."),
    BANK_READ_SUCCESS("BANK_READ_SUCCESS_200", HttpStatus.OK, "은행 정보를 성공적으로 조회했습니다."),
    BANK_LIST_SUCCESS("BANK_LIST_SUCCESS_200", HttpStatus.OK, "은행 목록을 성공적으로 조회했습니다."),
    BANK_UPDATE_SUCCESS("BANK_UPDATE_SUCCESS_200", HttpStatus.OK, "은행 정보가 성공적으로 수정되었습니다."),
    BANK_DELETE_SUCCESS("BANK_DELETE_SUCCESS_200", HttpStatus.OK, "은행이 성공적으로 삭제되었습니다."),

    // 은행 - 예외
    BANK_NOT_FOUND("BANK_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 은행을 찾을 수 없습니다."),

    // 머니 충전 - 예외
    ORDER_NOT_FOUND("ORDER_NOT_FOUND_404", HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다"),
    AMOUNT_MISMATCH("AMOUNT_MISMATCH_400", HttpStatus.BAD_REQUEST, "주문 금액이 일치하지 않습니다"),
    ALREADY_PROCESSED_ORDER("ALREADY_PROCESSED_ORDER_400", HttpStatus.BAD_REQUEST, "이미 처리된 주문입니다"),
    // 머니 충전 - 성공
    MONEY_RECHARGE_PREPARE_SUCCESS("MONEY_RECHARGE_PREPARE_SUCCESS_200", HttpStatus.OK, "머니 충전 요청에 성공했습니다"),

    // 지갑 - 예외
    WALLET_NOT_FOUND("WALLET_NOT_FOUND_404", HttpStatus.NOT_FOUND, "지갑 정보를 찾을 수 없습니다"),
    WALLET_ALREADY_EXISTS("WALLET_ALREADY_EXISTS_404", HttpStatus.NOT_FOUND, "이미 지갑이 있습니다"),
    INVALID_AMOUNT("INVALID_AMOUNT_400", HttpStatus.BAD_REQUEST, "금액은 0보다 커야합니다"),
    // 회원 - 예외
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없습니다."),

    // 카드 - 성공
    CARD_CREATE_SUCCESS("CARD_CREATE_SUCCESS_201", HttpStatus.CREATED, "카드가 성공적으로 등록되었습니다."),
    CARD_READ_SUCCESS("CARD_READ_SUCCESS_200", HttpStatus.OK, "카드 정보를 성공적으로 조회했습니다."),
    CARD_LIST_SUCCESS("CARD_LIST_SUCCESS_200", HttpStatus.OK, "카드 목록을 성공적으로 조회했습니다."),
    CARD_UPDATE_SUCCESS("CARD_UPDATE_SUCCESS_200", HttpStatus.OK, "카드 정보가 성공적으로 수정되었습니다."),
    CARD_DELETE_SUCCESS("CARD_DELETE_SUCCESS_200", HttpStatus.OK, "카드가 성공적으로 삭제되었습니다."),

    // 카드 - 실패
    CARD_NOT_FOUND("CARD_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 카드를 찾을 수 없습니다."),

    // 거래 관련
    TRADE_NOT_FOUND("TRADE_NOT_FOUND_404", HttpStatus.NOT_FOUND, "거래를 찾을 수 없습니다."),
    TRADE_STATUS_MISMATCH("TRADE_STATUS_MISMATCH_409", HttpStatus.CONFLICT, "현재 단계에서 수행할 수 없는 요청입니다."),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE_409", HttpStatus.CONFLICT, "잔액이 부족합니다."),
    DUPLICATE_TRADE_REQUEST("DUPLICATE_TRADE_REQUEST_409", HttpStatus.CONFLICT, "이미 요청된 거래가 있습니다."),
    TRADE_SELF_REQUEST("TRADE_SELF_REQUEST_400", HttpStatus.BAD_REQUEST, "자신의 글에는 거래를 요청할 수 없습니다.");


    private final String code;
    private final HttpStatus status;
    private final String message;
}
