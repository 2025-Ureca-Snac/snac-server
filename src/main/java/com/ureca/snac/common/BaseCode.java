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


    // 소셜 로그인 시도 - 성공
    OAUTH_LOGIN_SUCCESS("OAUTH_LOGIN_SUCCESS_200", HttpStatus.OK, "소셜 로그인에 성공했습니다."),

    // 소셜 로그인 시도 - 실패
    OAUTH_LOGIN_FAILED("OAUTH_LOGIN_FAILED_401", HttpStatus.UNAUTHORIZED, "회원이 아니거나, 소셜 연동이 되어 있지 않습니다."),

    // 로그아웃 시도 - 성공
    LOGOUT_SUCCESS("LOGOUT_SUCCESS_200", HttpStatus.OK, "로그아웃에 성공했습니다."),


    // 인증,인가
    TOKEN_EXPIRED("TOKEN_EXPIRED_401", HttpStatus.UNAUTHORIZED, "엑세스 토큰이 만료되었습니다."),
    SOCIAL_TOKEN_EXPIRED("SOCIAL_TOKEN_EXPIRED_401", HttpStatus.UNAUTHORIZED, "소셜 토큰이 만료되었습니다."),
    TOKEN_INVALID("TOKEN_INVALID_401", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    SOCIAL_TOKEN_INVALID("SOCIAL_TOKEN_INVALID_401", HttpStatus.UNAUTHORIZED, "유효하지 않은 소셜 토큰입니다."),
    TOKEN_SUCCESS("TOKEN_SUCCESS_200", HttpStatus.OK, "토큰이 성공적으로 발급되었습니다."),
    REISSUE_SUCCESS("REISSUE_SUCCESS_200", HttpStatus.OK, "액세스 토큰이 재발급되었습니다."),
    REFRESH_TOKEN_NULL("REFRESH_TOKEN_NULL_400", HttpStatus.BAD_REQUEST, "refresh 토큰이 없습니다."),
    REFRESH_TOKEN_EXPIRED("REFRESH_TOKEN_EXPIRED_400", HttpStatus.BAD_REQUEST, "refresh 토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN_400", HttpStatus.BAD_REQUEST, "유효하지 않은 refresh 토큰입니다."),

    // 문자 인증코드 발송- 성공
    SMS_VERIFICATION_SENT("SMS_VERIFICATION_SENT_200", HttpStatus.OK, "문자 인증번호가 발송되었습니다."),
    // 문자 인증코드 발송- 예외
    SMS_SEND_FAILED("SMS_SEND_FAILED_500", HttpStatus.INTERNAL_SERVER_ERROR, "문자 전송에 실패했습니다."),

    // 이메일 인증코드 발송- 성공
    EMAIL_VERIFICATION_SENT("EMAIL_VERIFICATION_SENT_200", HttpStatus.OK, "Email 인증번호가 발송되었습니다."),
    // 이메일 인증코드 발송- 예외
    EMAIL_SEND_FAILED("EMAIL_SEND_FAILED_500", HttpStatus.INTERNAL_SERVER_ERROR, "Email 인증번호 전송에 실패했습니다."),
    EMAIL_NOT_VERIFIED("EMAIL_NOT_VERIFIED_401", HttpStatus.UNAUTHORIZED, "Email 검증에 실패했습니다."),


    // 문자 인증코드 인증- 성공
    SMS_CODE_VERIFICATION_SUCCESS("SMS_CODE_VERIFICATION_SUCCESS_200", HttpStatus.OK, "문자 인증에 성공했습니다."),

    // 문자 인증코드 인증- 예외
    SMS_CODE_VERIFICATION_EXPIRED("SMS_CODE_VERIFICATION_EXPIRED_401", HttpStatus.UNAUTHORIZED, "문자 인증번호가 만료되었거나 존재하지 않습니다."),
    SMS_CODE_VERIFICATION_MISMATCH("SMS_CODE_VERIFICATION_MISMATCH_401", HttpStatus.UNAUTHORIZED, "문자 인증번호가 일치하지 않습니다."),

    //이메일 인증코드 인증- 성공
    EMAIL_CODE_VERIFICATION_SUCCESS("EMAIL_CODE_VERIFICATION_SUCCESS_200", HttpStatus.OK, "Email 인증에 성공했습니다."),

    //이메일 인증코드 인증- 예외
    EMAIL_CODE_VERIFICATION_EXPIRED("EMAIL_CODE_VERIFICATION_EXPIRED_401", HttpStatus.UNAUTHORIZED, "Email 인증번호가 만료되었거나 존재하지 않습니다."),
    EMAIL_CODE_VERIFICATION_MISMATCH("EMAIL_CODE_VERIFICATION_MISMATCH_401", HttpStatus.UNAUTHORIZED, "Email 인증번호가 일치하지 않습니다."),

    // 휴대폰번호 검증 - 실패
    PHONE_NOT_VERIFIED("PHONE_NOT_VERIFIED_422", HttpStatus.UNPROCESSABLE_ENTITY, "휴대폰 인증이 완료되지 않았습니다."),

    // 은행 - 성공
    BANK_CREATE_SUCCESS("BANK_CREATE_SUCCESS_201", HttpStatus.CREATED, "은행이 성공적으로 생성되었습니다."),
    BANK_READ_SUCCESS("BANK_READ_SUCCESS_200", HttpStatus.OK, "은행 정보를 성공적으로 조회했습니다."),
    BANK_LIST_SUCCESS("BANK_LIST_SUCCESS_200", HttpStatus.OK, "은행 목록을 성공적으로 조회했습니다."),
    BANK_UPDATE_SUCCESS("BANK_UPDATE_SUCCESS_200", HttpStatus.OK, "은행 정보가 성공적으로 수정되었습니다."),
    BANK_DELETE_SUCCESS("BANK_DELETE_SUCCESS_200", HttpStatus.OK, "은행이 성공적으로 삭제되었습니다."),

    // 은행 - 예외
    BANK_NOT_FOUND("BANK_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 은행을 찾을 수 없습니다."),

    // 결제 - 성공
    MONEY_RECHARGE_PREPARE_SUCCESS("MONEY_RECHARGE_PREPARE_SUCCESS_200", HttpStatus.OK, "머니 충전 요청에 성공했습니다"),
    PAYMENT_CANCEL_SUCCESS("PAYMENT_CANCEL_SUCCESS_200", HttpStatus.OK, "결제 취소에 성공했습니다,"),

    // 결제 - 예외
    PAYMENT_NOT_FOUND("PAYMENT_NOT_FOUND_404", HttpStatus.NOT_FOUND, "존재하지 않는 결제입니다"),
    ALREADY_PROCESSED_PAYMENT("ALREADY_PROCESSED_PAYMENT_409", HttpStatus.CONFLICT, "이미 처리된 결제입니다"),
    PAYMENT_OWNERSHIP_MISMATCH("PAYMENT_OWNERSHIP_MISMATCH_403", HttpStatus.FORBIDDEN, "해당 결제를 처리할 권한이 없습니다"),
    AMOUNT_MISMATCH("AMOUNT_MISMATCH_400", HttpStatus.BAD_REQUEST, "주문 금액이 일치하지 않습니다"),
    PAYMENT_NOT_CANCELLABLE("PAYMENT_NOT_CANCELLABLE_409", HttpStatus.CONFLICT, "결제를 취소할 수 없는 상태입니다"),
    INVALID_PAYMENT_FOR_RECHARGE("INVALID_PAYMENT_FOR_RECHARGE_409", HttpStatus.CONFLICT, "충전 기록을 생성할 수 없는 결제입니다"),

    // 토스 API
    TOSS_API_CALL_ERROR("TOSS_API_CALL_ERROR_500", HttpStatus.INTERNAL_SERVER_ERROR, "토스 결제 시스템 연동 중 오류 발생"),
    TOSS_API_CALL_ERROR_CUSTOM("TOSS_API_CALL_ERROR_CUSTOM_400", HttpStatus.BAD_REQUEST, "Toss API custom"),

    // 지갑 - 성공
    WALLET_SUMMARY_SUCCESS("WALLET_SUMMARY_SUCCESS_200", HttpStatus.OK, "내 지갑 요약 정보 조회 성공했습니다"),

    // 지갑 - 예외
    WALLET_NOT_FOUND("WALLET_NOT_FOUND_404", HttpStatus.NOT_FOUND, "지갑 정보를 찾을 수 없습니다"),
    WALLET_ALREADY_EXISTS("WALLET_ALREADY_EXISTS_409", HttpStatus.CONFLICT, "이미 지갑이 있습니다"),
    INVALID_AMOUNT("INVALID_AMOUNT_400", HttpStatus.BAD_REQUEST, "금액은 0보다 커야합니다"),
    INSUFFICIENT_BALANCE("INSUFFICIENT_BALANCE_400", HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),

    // 단골 - 성공
    FAVORITE_CREATE_SUCCESS("FAVORITE_CREATE_SUCCESS_201", HttpStatus.CREATED, "단골 등록에 성공했습니다."),
    FAVORITE_LIST_SUCCESS("FAVORITE_LIST_SUCCESS_200", HttpStatus.OK, "단골 목록 조회에 성공했습니다."),
    FAVORITE_DELETE_SUCCESS("FAVORITE_DELETE_SUCCESS_200", HttpStatus.OK, "단골 삭제에 성공했습니다."),

    // 단골 - 예외
    CANNOT_FAVORITE_SELF("CANNOT_FAVORITE_SELF_400", HttpStatus.BAD_REQUEST, "자기 자신을 단골로 등록할 수 없습니다."),
    ALREADY_FAVORITE_MEMBER("ALREADY_FAVORITE_MEMBER_409", HttpStatus.CONFLICT, "이미 단골로 등록된 사용자 입니다."),
    FAVORITE_RELATION_NOT_FOUND("FAVORITE_RELATION_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 단골 관계를 찾을 수 없슨니다"),

    // 거래 내역 - 성공
    ASSET_HISTORY_SUCCESS("ASSET_HISTORY_SUCCESS_200", HttpStatus.OK, "자산 내역 조회 성공했습니다"),
    // 거래 내역 - 예외
    ASSET_HISTORY_NOT_FOUND("ASSET_HISTORY_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 자산 내역을 차즐 수 없습니다"),

    // 거래 내역 생성 - 예외
    INVALID_ASSET_AMOUNT("INVALID_ASSET_AMOUNT_400", HttpStatus.BAD_REQUEST, "거래 금액은 0보다 커야 합니다."),
    INVALID_ASSET_BALANCE("INVALID_ASSET_BALANCE_400", HttpStatus.BAD_REQUEST, "거래 후 잔액은은 0이상 입니다."),
    INVALID_ASSET_TITLE("INVALID_ASSET_TITLE_400", HttpStatus.BAD_REQUEST, "거래 제목 1자 이상 50자 이하 입니다"),
    INVALID_ASSET_SOURCE("INVALID_ASSET_SOURCE_400", HttpStatus.BAD_REQUEST, "거래 출처 정보가 유효하지 않습니다"),
    INCONSISTENT_TRANSACTION_TYPE("INCONSISTENT_TRANSACTION_TYPE_409", HttpStatus.CONFLICT, "거래 타입과 카테고리가 일치하지 않습니다"),
    INVALID_ASSET_CATEGORY_COMBINATION("INVALID_ASSET_CATEGORY_COMBINATION_409", HttpStatus.CONFLICT, "자산 타입과 카테고리의 조합이 유효하지 않습니다"),

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
    CARD_ALREADY_TRADING("CARD_ALREADY_TRADING_400", HttpStatus.BAD_REQUEST, "이미 거래 중인 카드입니다."),
    CARD_ALREADY_SELLING("CARD_ALREADY_SELLING_400", HttpStatus.BAD_REQUEST, "이미 판매 중인 카드입니다."),
    CARD_INVALID_STATUS("CARD_INVALID_STATUS_400", HttpStatus.BAD_REQUEST, "카드 상태가 유효하지 않습니다."),
    CARD_ALREADY_SOLD_OUT("CARD_ALREADY_SOLD_OUT_400", HttpStatus.BAD_REQUEST, "이미 판매 완료된 카드입니다."),

    // 거래 - 성공
    TRADE_CREATE_SUCCESS("TRADE_CREATE_SUCCESS_201", HttpStatus.CREATED, "거래가 정상적으로 생성되었습니다."),
    TRADE_REQUEST_SUCCESS("TRADE_REQUEST_SUCCESS_200", HttpStatus.OK, "거래 요청에 성공하였습니다."),
    TRADE_ACCEPT_SUCCESS("TRADE_ACCEPT_SUCCESS_200", HttpStatus.OK, "거래 수락에 성공하였습니다."),
    TRADE_CANCEL_SUCCESS("TRADE_CANCEL_SUCCESS_200", HttpStatus.OK, "거래 취소에 성공하였습니다."),
    TRADE_DATA_SENT_SUCCESS("TRADE_DATA_SENT_SUCCESS_200", HttpStatus.OK, "거래 데이터 전송에 성공하였습니다."),
    TRADE_CONFIRM_SUCCESS("TRADE_CONFIRM_SUCCESS_200", HttpStatus.OK, "거래 확정에 성공하였습니다."),
    TRADE_SCROLL_SUCCESS("TRADE_SCROLL_SUCCESS_200", HttpStatus.OK, "거래 내역 조회에 성공하였습니다."),
    TRADE_PROGRESS_COUNT_SUCCESS("TRADE_PROGRESS_COUNT_SUCCESS_200", HttpStatus.OK, "진행 중인 거래 건수를 성공적으로 조회했습니다."),
    TRADE_STATISTICS_READ_SUCCESS("STATISTICS_READ_SUCCESS_200", HttpStatus.OK, "거래 통계 데이터를 성공적으로 조회했습니다."),

    // 거래 - 실패
    TRADE_NOT_FOUND("TRADE_NOT_FOUND_404", HttpStatus.NOT_FOUND, "거래를 찾을 수 없습니다."),
    TRADE_STATUS_MISMATCH("TRADE_STATUS_MISMATCH_409", HttpStatus.CONFLICT, "현재 단계에서 수행할 수 없는 요청입니다."),
    DUPLICATE_TRADE_REQUEST("DUPLICATE_TRADE_REQUEST_409", HttpStatus.CONFLICT, "이미 요청된 거래가 있습니다."),
    TRADE_SELF_REQUEST("TRADE_SELF_REQUEST_400", HttpStatus.BAD_REQUEST, "자신의 글에는 거래를 요청할 수 없습니다."),
    TRADE_PERMISSION_DENIED("TRADE_PERMISSION_DENIED_403", HttpStatus.FORBIDDEN, "거래를 진행할 권한이 없습니다."),

    TRADE_PAYMENT_MISMATCH("TRADE_PAYMENT_MISMATCH_400", HttpStatus.BAD_REQUEST, "결제 금액이 카드 가격과 일치하지 않습니다."),
    TRADE_CANCEL_NOT_ALLOWED("TRADE_CANCEL_NOT_ALLOWED_400", HttpStatus.BAD_REQUEST, "취소할 수 없는 거래 상태입니다."),
    TRADE_CANCEL_PERMISSION_DENIED("TRADE_CANCEL_PERMISSION_DENIED_403", HttpStatus.FORBIDDEN, "해당 거래를 취소할 권한이 없습니다."),
    TRADE_INVALID_STATUS("TRADE_INVALID_STATUS_400", HttpStatus.BAD_REQUEST, "잘못된 거래 상태입니다."),
    TRADE_SEND_PERMISSION_DENIED("TRADE_SEND_PERMISSION_DENIED_403", HttpStatus.FORBIDDEN, "판매자만 거래 데이터를 전송할 수 있습니다."),
    TRADE_CONFIRM_PERMISSION_DENIED("TRADE_CONFIRM_PERMISSION_DENIED_403", HttpStatus.FORBIDDEN, "구매자만 거래를 완료할 수 있습니다."),
    TRADE_STATISTICS_NOT_FOUND("STATISTICS_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 통신사의 통계 데이터가 존재하지 않습니다."),


    // 계좌 - 성공
    ACCOUNT_CREATE_SUCCESS("ACCOUNT_CREATE_SUCCESS_201", HttpStatus.CREATED, "계좌가 성공적으로 생성되었습니다."),
    ACCOUNT_READ_SUCCESS("ACCOUNT_READ_SUCCESS_200", HttpStatus.OK, "계좌 정보를 성공적으로 조회했습니다."),
    ACCOUNT_LIST_SUCCESS("ACCOUNT_LIST_SUCCESS_200", HttpStatus.OK, "계좌 목록을 성공적으로 조회했습니다."),
    ACCOUNT_UPDATE_SUCCESS("ACCOUNT_UPDATE_SUCCESS_200", HttpStatus.OK, "계좌 정보가 성공적으로 수정되었습니다."),
    ACCOUNT_DELETE_SUCCESS("ACCOUNT_DELETE_SUCCESS_200", HttpStatus.OK, "계좌가 성공적으로 삭제되었습니다."),

    // 계좌 - 예외
    ACCOUNT_NOT_FOUND("ACCOUNT_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 계좌를 찾을 수 없습니다."),

    // 거래 이미지 첨부
    ATTACHMENT_ALREADY_EXISTS("ATTACHMENT_ALREADY_EXISTS_409", HttpStatus.CONFLICT, "이미 해당 거래에 첨부 이미지가 존재합니다."),
    ATTACHMENT_NOT_FOUND("ATTACHMENT_NOT_FOUND_404", HttpStatus.NOT_FOUND, "첨부된 이미지를 찾을 수 없습니다."),
    ATTACHMENT_PERMISSION_DENIED("ATTACHMENT_PERMISSION_DENIED_403", HttpStatus.FORBIDDEN, "이미지를 업로드할 권한이 없습니다."),

    // S3
    S3_UPLOAD_FAILED("S3_UPLOAD_FAILED_500", HttpStatus.INTERNAL_SERVER_ERROR, "S3 업로드에 실패했습니다."),
    ATTACHMENT_UPLOAD_SUCCESS("ATTACHMENT_UPLOAD_SUCCESS_201", HttpStatus.CREATED, "이미지가 성공적으로 업로드되었습니다."),
    ATTACHMENT_PRESIGNED_URL_ISSUED("ATTACHMENT_PRESIGNED_URL_ISSUED_200", HttpStatus.OK, "첨부 이미지에 대한 접근 URL이 발급되었습니다.");
    private final String code;
    private final HttpStatus status;
    private final String message;
}
