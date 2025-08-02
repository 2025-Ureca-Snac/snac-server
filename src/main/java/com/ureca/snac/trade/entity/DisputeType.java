package com.ureca.snac.trade.entity;

public enum DisputeType {
    DATA_NONE, // 데이터 안옴 (구매자 입장)
    DATA_PARTIAL, // 일부만 수신 (구매자 입장)
    PAYMENT, // 결제 관련
    ACCOUNT, // 계정 관련
    TECHNICAL_PROBLEM, // 기술적 문제
    REPORT_OTHER, // 기타
    QNA_OTHER // 기타
}