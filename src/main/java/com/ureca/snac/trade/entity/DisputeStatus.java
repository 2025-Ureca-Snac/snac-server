package com.ureca.snac.trade.entity;

public enum DisputeStatus {
    OPEN,        // 접수 완료, 대기 중
    IN_REVIEW,   // 처리 중, 담당자 배정?
    AWAITING_USER, // 추가 요청, 첨부파일 등
    RESOLVED,       // 해결 완료
    REJECTED        // 기각
}