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
    NICKNAME_DUPLICATE("NICKNAME_DUPLICATE_409", HttpStatus.CONFLICT, "이미 사용중인 닉네임입니다."),

    // 로그인 시도 - 성공
    LOGIN_SUCCESS("LOGIN_SUCCESS_200", HttpStatus.OK, "로그인에 성공했습니다."),

    // 로그인 시도 - 실패
    LOGIN_FAILED("LOGIN_FAILED_401", HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),


    // 소셜 로그인 시도 - 성공
    OAUTH_LOGIN_SUCCESS("OAUTH_LOGIN_SUCCESS_200", HttpStatus.OK, "소셜 로그인에 성공했습니다."),

    // 소셜 로그인 시도 - 실패
    UNSUPPORTED_SOCIAL_PROVIDER("UNSUPPORTED_SOCIAL_PROVIDER_400", HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 프로바이더입니다."),

    OAUTH_DB_ALREADY_LINKED("OAUTH_DB_ALREADY_LINKED_409", HttpStatus.CONFLICT, "이미 다른 계정에 연동된 소셜 계정입니다."),
    OAUTH_DB_ACCOUNT_NOT_FOUND("OAUTH_DB_ACCOUNT_NOT_FOUND_404", HttpStatus.NOT_FOUND, "소셜 계정에 연동된 계정이 없습니다. 회원가입을 먼저 진행해주세요."),

    // 카카오 연동 해제 관련
    KAKAO_UNLINK_SUCCESS("KAKAO_UNLINK_SUCCESS_200", HttpStatus.OK, "카카오 연결 끊기에 성공했습니다."),
    KAKAO_UNLINK_FAILED("KAKAO_UNLINK_FAILED_500", HttpStatus.INTERNAL_SERVER_ERROR, "카카오 연결 끊기에 실패했습니다."),
    KAKAO_NO_LINKED("KAKAO_NO_LINKED_400", HttpStatus.BAD_REQUEST, "카카오 연동이 되어있지 않습니다."),
    KAKAO_API_CALL_ERROR("KAKAO_API_CALL_ERROR_500", HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API 연동 중 오류가 발생했습니다."),

    // 네이버 연동 해제 관련
    NAVER_UNLINK_SUCCESS("NAVER_UNLINK_SUCCESS_200", HttpStatus.OK, "네이버 연결 끊기에 성공했습니다."),
    NAVER_NO_LINKED("NAVER_NO_LINKED_400", HttpStatus.BAD_REQUEST, "네이버 연동이 되어있지 않습니다."),
    NAVER_TOKEN_NOT_FOUND("NAVER_TOKEN_NOT_FOUND_400", HttpStatus.BAD_REQUEST, "Naver access token이 없습니다."),
    NAVER_API_CALL_ERROR("NAVER_API_CALL_ERROR_500", HttpStatus.INTERNAL_SERVER_ERROR, "네이버 API 연동 중 오류가 발생했습니다."),


    // Google 연동 해제 관련
    GOOGLE_UNLINK_SUCCESS("GOOGLE_UNLINK_SUCCESS_200", HttpStatus.OK, "Google 연결 끊기에 성공했습니다."),
    GOOGLE_NO_LINKED("GOOGLE_NO_LINKED_400", HttpStatus.BAD_REQUEST, "Google 연동이 되어있지 않습니다."),
    GOOGLE_UNLINK_FAILED("GOOGLE_UNLINK_FAILED_500", HttpStatus.INTERNAL_SERVER_ERROR, "Google 연결 끊기에 실패했습니다."),
    GOOGLE_TOKEN_NOT_FOUND("GOOGLE_TOKEN_NOT_FOUND_400", HttpStatus.BAD_REQUEST, "Google access token이 없습니다."),


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
    // 이메일 휴대폰번호로 조회 - 성공
    EMAIL_FOUND_BY_PHONE("EMAIL_FOUND_BY_PHONE_200", HttpStatus.OK, "휴대폰 번호로 EMAIL 을 찾았습니다"),
    // 이메일
    EMAIL_IS_EXIST("EMAIL_IS_EXIST_200", HttpStatus.OK, "이메일이 있는지 확인하였습니다."),

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

    // 닉네임 사용 가능
    NICKNAME_AVAILABLE("NICKNAME_AVAILABLE_200", HttpStatus.OK, "사용 가능한 닉네임입니다."),

    // 닉네임 변경 - 성공
    NICKNAME_CHANGED("NICKNAME_CHANGED_200", HttpStatus.OK, "닉네임 변경이 성공하였습니다."),

    // 닉네임 변경 시간 검증 - 실패
    NICKNAME_CHANGE_TOO_EARLY("NICKNAME_CHANGE_TOO_EARLY_400", HttpStatus.BAD_REQUEST, "닉네임은 최근 변경 시점 24시간이 지난 후 수정할 수 있습니다."),

    // 게시글 - 성공
    ARTICLE_CREATE_SUCCESS("ARTICLE_CREATE_SUCCESS_201", HttpStatus.CREATED, "게시글이 성공적으로 등록되었습니다."),
    ARTICLE_READ_SUCCESS("ARTICLE_READ_SUCCESS_200", HttpStatus.OK, "게시글을 성공적으로 조회했습니다."),
    ARTICLE_LIST_SUCCESS("ARTICLE_LIST_SUCCESS_200", HttpStatus.OK, "게시글 목록을 성공적으로 조회했습니다."),
    ARTICLE_UPDATE_SUCCESS("ARTICLE_UPDATE_SUCCESS_200", HttpStatus.OK, "게시글이 성공적으로 수정되었습니다."),
    ARTICLE_DELETE_SUCCESS("ARTICLE_DELETE_SUCCESS_200", HttpStatus.OK, "게시글이 성공적으로 삭제되었습니다."),
    ARTICLE_COUNT_SUCCESS("ARTICLE_COUNT_SUCCESS_200", HttpStatus.OK, "전체 게시글 수를 성공적으로 조회했습니다."),


    // 게시글 - 실패
    ARTICLE_NOT_FOUND("ARTICLE_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    ARTICLE_PERMISSION_DENIED("ARTICLE_PERMISSION_DENIED_403", HttpStatus.FORBIDDEN, "해당 게시글에 대한 권한이 없습니다."),

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
    MONEY_RECHARGE_SUCCESS("MONEY_RECHARGE_SUCCESS_200", HttpStatus.OK, "머니 충전에 성공했습니다"),
    PAYMENT_CANCEL_SUCCESS("PAYMENT_CANCEL_SUCCESS_200", HttpStatus.OK, "결제 취소에 성공했습니다,"),

    PAYMENT_FAILURE_LOGGED("PAYMENT_FAILURE_LOGGED_200", HttpStatus.OK, "결제 실패 내역 기록이 성공했습니다,"),

    // 결제 - 예외
    PAYMENT_NOT_FOUND("PAYMENT_NOT_FOUND_404", HttpStatus.NOT_FOUND, "존재하지 않는 결제입니다"),
    PAYMENT_ALREADY_PROCESSED_PAYMENT("PAYMENT_ALREADY_PROCESSED_PAYMENT_409", HttpStatus.CONFLICT, "이미 처리된 결제입니다"),
    PAYMENT_OWNERSHIP_MISMATCH("PAYMENT_OWNERSHIP_MISMATCH_403", HttpStatus.FORBIDDEN, "해당 결제를 처리할 권한이 없습니다"),
    PAYMENT_AMOUNT_MISMATCH("PAYMENT_AMOUNT_MISMATCH_400", HttpStatus.BAD_REQUEST, "주문 금액이 일치하지 않습니다"),
    PAYMENT_NOT_CANCELLABLE("PAYMENT_NOT_CANCELLABLE_409", HttpStatus.CONFLICT, "결제를 취소할 수 없는 상태입니다"),
    INVALID_PAYMENT_FOR_RECHARGE("INVALID_PAYMENT_FOR_RECHARGE_409", HttpStatus.CONFLICT, "충전 기록을 생성할 수 없는 결제입니다"),
    PAYMENT_PERIOD_EXPIRED("PAYMENT_PERIOD_EXPIRED_400", HttpStatus.BAD_REQUEST, "취소 가능한 기간 지났습니다"),

    PAYMENT_INTERNAL_ERROR("PAYMENT_INTERNAL_ERROR_500", HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 중 심각한 내부 오류가 발생했습니다 " +
            "관리자에게 문의주세요"),
    // 결제 취소 - 예외
    ALREADY_USED_RECHARGE_CANNOT_CANCEL("ALREADY_USED_RECHARGE_CANNOT_CANCEL_409", HttpStatus.CONFLICT, "이미 사용된 내역이 있어 취소할 수 없습니다"),

    // 토스 API - 예외
    TOSS_INVALID_CARD_INFO("TOSS_INVALID_CARD_INFO_400", HttpStatus.BAD_REQUEST, "카드 정보가 유효하지 않습니다. 카드번호나 유효기간 확인해주세요"),
    TOSS_NOT_ENOUGH_BALANCE("TOSS_NOT_ENOUGH_BALANCE_400", HttpStatus.BAD_REQUEST, "카드 잔액이 부족합니다"),
    TOSS_INVALID_API_KEY("TOSS_INVALID_API_KEY_500", HttpStatus.INTERNAL_SERVER_ERROR,
            "결제 연동 설정에 문제가 발생했습니다. 관리자에게 문의주세요"),
    TOSS_API_CALL_ERROR("TOSS_API_CALL_ERROR_500", HttpStatus.INTERNAL_SERVER_ERROR, "토스 결제 시스템 연동 중 오류 발생"),

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
    FAVORITE_CHECK_SUCCESS("FAVORITE_CHECK_SUCCESS_200", HttpStatus.OK, "단골 여부 조회에 성공했습니다."),
    FAVORITE_DELETE_SUCCESS("FAVORITE_DELETE_SUCCESS_200", HttpStatus.OK, "단골 삭제에 성공했습니다."),

    // 단골 - 예외
    CANNOT_FAVORITE_SELF("CANNOT_FAVORITE_SELF_400", HttpStatus.BAD_REQUEST, "자기 자신을 단골로 등록할 수 없습니다."),
    ALREADY_FAVORITE_MEMBER("ALREADY_FAVORITE_MEMBER_409", HttpStatus.CONFLICT, "이미 단골로 등록된 사용자 입니다."),
    FAVORITE_RELATION_NOT_FOUND("FAVORITE_RELATION_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 단골 관계를 찾을 수 없슨니다"),

    // 거래 내역 - 성공
    ASSET_HISTORY_SUCCESS("ASSET_HISTORY_SUCCESS_200", HttpStatus.OK, "자산 내역 조회 성공했습니다"),
    // 거래 내역 - 예외
    ASSET_HISTORY_NOT_FOUND("ASSET_HISTORY_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 자산 내역을 차즐 수 없습니다"),

    // 정산 - 성공
    SETTLEMENT_SUCCESS("SETTLEMENT_SUCCESS_200", HttpStatus.OK, "정산 요청이 성공되었습니다"),
    // 정산 - 예외
    SETTLEMENT_ACCOUNT_MISMATCH("SETTLEMENT_ACCOUNT_MISMATCH_400", HttpStatus.BAD_REQUEST, "입력하신 계좌번호가 등록된 정보와 일치하지 않습니다"),
    INVALID_SETTLEMENT_REQUEST("INVALID_SETTLEMENT_REQUEST_400", HttpStatus.BAD_REQUEST, "정산 요청 정보가 올바르지 않습니다"),

    // 거래 내역 생성 - 예외
    INVALID_ASSET_AMOUNT("INVALID_ASSET_AMOUNT_400", HttpStatus.BAD_REQUEST, "거래 금액은 0보다 커야 합니다."),
    INVALID_ASSET_BALANCE("INVALID_ASSET_BALANCE_400", HttpStatus.BAD_REQUEST, "거래 후 잔액은은 0이상 입니다."),
    INVALID_ASSET_TITLE("INVALID_ASSET_TITLE_400", HttpStatus.BAD_REQUEST, "거래 제목 1자 이상 50자 이하 입니다"),
    INVALID_ASSET_SOURCE("INVALID_ASSET_SOURCE_400", HttpStatus.BAD_REQUEST, "거래 출처 정보가 유효하지 않습니다"),
    INCONSISTENT_TRANSACTION_TYPE("INCONSISTENT_TRANSACTION_TYPE_409", HttpStatus.CONFLICT, "거래 타입과 카테고리가 일치하지 않습니다"),
    INVALID_ASSET_CATEGORY_COMBINATION("INVALID_ASSET_CATEGORY_COMBINATION_409", HttpStatus.CONFLICT, "자산 타입과 카테고리의 조합이 유효하지 않습니다"),

    // 회원 - 성공
    MEMBER_COUNT_SUCCESS("MEMBER_COUNT_SUCCESS_200", HttpStatus.OK, "전체 회원 수를 성공적으로 조회했습니다."),

    // 회원 - 예외
    MEMBER_NOT_FOUND("MEMBER_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없습니다."),

    // 마이페이지 - 성공
    MYPAGE_GET_SUCCESS("MYPAGE_GET_SUCCESS_200", HttpStatus.OK, "마이페이지 정보 조회에 성공했습니다."),

    // 비밀번호 변경 – 성공
    PASSWORD_CHANGED("PASSWORD_CHANGED_200", HttpStatus.OK, "비밀번호가 변경되었습니다."),

    // 전화번호 변경 – 성공
    PHONE_CHANGED("PHONE_CHANGED_200", HttpStatus.OK, "전화번호가 변경되었습니다."),

    // 전화번호 동일 확인 – 성공
    PHONE_EXIST_SUCCESS("PHONE_EXIST_SUCCESS_200", HttpStatus.OK, "전화번호 변경이 가능합니다."),

    // 비밀번호 변경 – 예외
    INVALID_CURRENT_PASSWORD("INVALID_CURRENT_PASSWORD_400", HttpStatus.BAD_REQUEST, "현재 비밀번호가 올바르지 않습니다."),

    // 전화번호 변경 – 예외
    INVALID_CURRENT_PHONE("INVALID_CURRENT_PASSWORD_400", HttpStatus.BAD_REQUEST, "현재 전화번호가 올바르지 않습니다."),

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
    NOT_REALTIME_SELL_CARD("NOT_REALTIME_SELL_CARD_400", HttpStatus.BAD_REQUEST, "실시간 판매 카드가 아닙니다."),

    // 거래 - 성공
    TRADE_CREATE_SUCCESS("TRADE_CREATE_SUCCESS_201", HttpStatus.CREATED, "거래가 정상적으로 생성되었습니다."),
    TRADE_REQUEST_SUCCESS("TRADE_REQUEST_SUCCESS_200", HttpStatus.OK, "거래 요청에 성공하였습니다."),
    TRADE_ACCEPT_SUCCESS("TRADE_ACCEPT_SUCCESS_200", HttpStatus.OK, "거래 수락에 성공하였습니다."),
    TRADE_CANCEL_SUCCESS("TRADE_CANCEL_SUCCESS_200", HttpStatus.OK, "거래 취소에 성공하였습니다."),
    TRADE_DATA_SENT_SUCCESS("TRADE_DATA_SENT_SUCCESS_200", HttpStatus.OK, "이미지 검증 및 거래 데이터 전송에 성공하였습니다."),
    TRADE_CONFIRM_SUCCESS("TRADE_CONFIRM_SUCCESS_200", HttpStatus.OK, "거래 확정에 성공하였습니다."),
    TRADE_SCROLL_SUCCESS("TRADE_SCROLL_SUCCESS_200", HttpStatus.OK, "거래 내역 조회에 성공하였습니다."),
    TRADE_PROGRESS_COUNT_SUCCESS("TRADE_PROGRESS_COUNT_SUCCESS_200", HttpStatus.OK, "진행 중인 거래 건수를 성공적으로 조회했습니다."),
    TRADE_STATISTICS_READ_SUCCESS("STATISTICS_READ_SUCCESS_200", HttpStatus.OK, "거래 통계 데이터를 성공적으로 조회했습니다."),
    TRADE_READ_SUCCESS("TRADE_READ_SUCCESS_200", HttpStatus.OK, "거래 정보를 성공적으로 조회했습니다."),

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

    // LMM
    IMAGE_CRITERIA_REJECTED("IMAGE_CRITERIA_REJECTED_400", HttpStatus.BAD_REQUEST, "이미지가 기준을 만족하지 않아 거부되었습니다."),
    IMAGE_VALIDATION_LLM_ERROR("IMAGE_VALIDATION_LLM_ERROR_500", HttpStatus.INTERNAL_SERVER_ERROR, "LLM 호출 중 오류가 발생했습니다."),
    IMAGE_VALIDATION_LLM_ERROR_TIMEOUT("IMAGE_VALIDATION_LLM_ERROR_TIMEOUT_504", HttpStatus.GATEWAY_TIMEOUT, "이미지 검증 중 타임아웃 되었습니다."),
    IMAGE_VALIDATION_RESPONSE_PARSE_ERROR("IMAGE_VALIDATION_RESPONSE_PARSE_ERROR_500", HttpStatus.INTERNAL_SERVER_ERROR, "이미지 검증 응답 파싱에 실패했습니다."),
    IMAGE_VALIDATION_INVALID_RESULT("IMAGE_VALIDATION_INVALID_RESULT_400", HttpStatus.BAD_REQUEST, "이미지 검증 결과가 유효하지 않습니다."),
    IMAGE_NOT_SUPPORT_TYPE("IMAGE_NOT_SUPPORT_TYPE_505", HttpStatus.HTTP_VERSION_NOT_SUPPORTED, "지원하지 않는 이미지 형식입니다."),

    // 취소 플로우
    TRADE_CANCEL_REQUESTED("TRADE_CANCEL_REQUESTED_200", HttpStatus.OK, "거래 취소 요청이 등록되었습니다."),
    TRADE_CANCEL_ACCEPTED("TRADE_CANCEL_ACCEPTED_200", HttpStatus.OK, "거래 취소가 승인되었습니다."),
    TRADE_CANCEL_REJECTED("TRADE_CANCEL_REJECTED_200", HttpStatus.OK, "거래 취소 요청이 거절되었습니다."),
    TRADE_ALREADY_CANCEL_REQUESTED("TRADE_ALREADY_CANCEL_REQUESTED_409", HttpStatus.CONFLICT, "이미 취소 요청 기록이 있습니다."),
    TRADE_CANCEL_NOT_FOUND("TRADE_CANCEL_NOT_FOUND_404", HttpStatus.NOT_FOUND, "취소 요청 정보를 찾을 수 없습니다."),

    // 스케줄러 자동 처리
    AUTO_REFUND_SUCCESS("AUTO_REFUND_SUCCESS_200", HttpStatus.OK, "판매자 지연으로 자동 환불되었습니다."),
    AUTO_PAYOUT_SUCCESS("AUTO_PAYOUT_SUCCESS_200", HttpStatus.OK, "구매자 미확정으로 자동 정산되었습니다."),

    // 거래 소요 시간 통계 - 성공
    TRADE_DURATION_STATISTIC_READ_SUCCESS("TRADE_DURATION_STATISTIC_READ_SUCCESS_200", HttpStatus.OK, "거래 소요 시간 통계 데이터를 성공적으로 조회했습니다."),

    // 거래 소요 시간 통계 - 실패
    TRADE_DURATION_STATISTIC_NOT_FOUND("TRADE_DURATION_STATISTIC_NOT_FOUND_404", HttpStatus.NOT_FOUND, "해당 거래 소요 시간 통계 데이터를 찾을 수 없습니다."),

    // 신고 예외
    DISPUTE_PERMISSION_DENIED("DISPUTE_PERMISSION_DENIED_403", HttpStatus.FORBIDDEN, "거래 당사자만 신고할 수 있습니다."),
    DISPUTE_ADMIN_PERMISSION_DENIED("DISPUTE_ADMIN_PERMISSION_DENIED_403", HttpStatus.FORBIDDEN, "관리자 아니야"),
    DISPUTE_NOT_FOUND("DISPUTE_NOT_FOUND_404", HttpStatus.NOT_FOUND, "신고를 찾을 수 없습니다."),
    DISPUTE_COMMENT_PERMISSION_DENIED("DISPUTE_COMMENT_PERMISSION_DENIED_403", HttpStatus.FORBIDDEN, "답변 작성 권한이 없습니다."),
    // 신고 성공
    DISPUTE_CREATE_SUCCESS("DISPUTE_CREATE_SUCCESS_201", HttpStatus.CREATED, "신고가 접수되었습니다."),
    QNA_CREATE_SUCCESS("QNA_CREATE_SUCCESS_201", HttpStatus.CREATED, "문의가 접수되었습니다."),
    DISPUTE_DETAIL_SUCCESS("DISPUTE_DETAIL_SUCCESS_200", HttpStatus.OK, "신고 상세를 조회했습니다."),
    DISPUTE_COMMENT_SUCCESS("DISPUTE_COMMENT_SUCCESS_201", HttpStatus.CREATED, "신고 답변이 등록되었습니다."),

    DISPUTE_NEED_MORE("DISPUTE_NEED_MORE_200", HttpStatus.OK,"추가 자료 요청"),
    DISPUTE_ANSWERED_SUCCESS("DISPUTE_ANSWERED_SUCCESS_200", HttpStatus.OK,"신고가 처리되었습니다."),
    DISPUTE_REJECTED_SUCCESS("DISPUTE_REJECTED_SUCCESS_200", HttpStatus.OK,"신고 기각 처리되었습니다."),
    DISPUTE_COMMENT_READ_SUCCESS ("DISPUTE_COMMENT_READ_SUCCESS_200",HttpStatus.OK,"신고 답변 조회 성공"),
    DISPUTE_MY_LIST_SUCCESS ("DISPUTE_MY_LIST_SUCCESS_SUCCESS_200",HttpStatus.OK,"내 신고 조회 성공"),
    DISPUTE_RECEIVED_LIST_SUCCESS ("DISPUTE_RECEIVED_LIST_SUCCESS_200",HttpStatus.OK,"신고 받은 내역 조회 성공"),

    DISPUTE_REFUND_AND_CANCEL_SUCCESS ("DISPUTE_REFUND_AND_CANCEL_SUCCESS_200", HttpStatus.OK, "환불 및 거래 취소가 완료되었습니다."),
    DISPUTE_PENALTY_GIVEN("DISPUTE_PENALTY_GIVEN_200",HttpStatus.OK, "판매자에게 패널티를 부여했습니다."),
    DISPUTE_FINALIZE_RESTORED("DISPUTE_FINALIZE_RESTORED_200",HttpStatus.OK, "활성 신고가 없어 거래 상태를 복구했습니다."),
    DISPUTE_FINALIZE_SKIPPED("DISPUTE_FINALIZE_SKIPPED_200",HttpStatus.OK, "활성 신고가 남아 있어 복구를 건너뜁니다."),
    TRADE_ALREADY_CANCELED("TRADE_ALREADY_CANCELED_409",HttpStatus.CONFLICT, "이미 취소된 거래입니다."),
    DISPUTE_STATE_NOT_ANSWERED("DISPUTE_STATE_NOT_ANSWERED_409",HttpStatus.CONFLICT, "답변 완료 상태가 아니므로 수행할 수 없습니다."),

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
    S3_DELETE_FAILED("S3_DELETE_FAILED_500", HttpStatus.INTERNAL_SERVER_ERROR, "S3 삭제에 실패했습니다"),
    ATTACHMENT_UPLOAD_SUCCESS("ATTACHMENT_UPLOAD_SUCCESS_201", HttpStatus.CREATED, "이미지가 성공적으로 업로드되었습니다."),
    ATTACHMENT_PRESIGNED_URL_ISSUED("ATTACHMENT_PRESIGNED_URL_ISSUED_200", HttpStatus.OK, "첨부 이미지에 대한 접근 URL이 발급되었습니다."),
    ATTACHMENT_PRESIGNED_UPLOAD_URL_ISSUED("ATTACHMENT_PRESIGNED__UPLOAD_URL_ISSUED_200", HttpStatus.OK, "s3 업로드를 위한 URL이 발급되었습니다.");
    private final String code;
    private final HttpStatus status;
    private final String message;
}
