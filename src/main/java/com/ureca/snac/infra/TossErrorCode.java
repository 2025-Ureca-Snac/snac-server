package com.ureca.snac.infra;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TossErrorCode {
    // 4xx 에러
    INVALID_CARD_EXPIRATION("INVALID_CARD_EXPIRATION"),
    INVALID_CARD_NUMBER("INVALID_CARD_NUMBER"),
    REJECT_CARD_COMPANY("REJECT_CARD_COMPANY"),
    NOT_ENOUGH_BALANCE("NOT_ENOUGH_BALANCE"),

    // 5xx 에러
    INVALID_API_KEY("INVALID_API_KEY"),
    UNAUTHORIZED_KEY("UNAUTHORIZED_KEY"),
    INVALID_AUTHORIZATION("INVALID_AUTHORIZATION"),

    // 특수 케이스
    ALREADY_PROCESSED_PAYMENT("ALREADY_PROCESSED_PAYMENT"),

    // 정의되지않은 에러 코드
    UNKNOWN("UNKNOWN_ERROR");

    private final String code;

    public static TossErrorCode fromCode(String codeString) {
        for (TossErrorCode errorCode : TossErrorCode.values()) {
            if (errorCode.getCode().equals(codeString)) {
                return errorCode;
            }
        }
        return UNKNOWN;
    }
}
