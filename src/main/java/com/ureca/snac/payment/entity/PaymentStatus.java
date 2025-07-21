package com.ureca.snac.payment.entity;

public enum PaymentStatus {
    PENDING,
    SUCCESS,
    CANCELED,
    FAIL
    /*
     * PENDING: 결제 대기중
     * SUCCESS : 결제 성공
     * CANCELED : 결제 취소
     * FAIL : 결제 실패
     */
}
