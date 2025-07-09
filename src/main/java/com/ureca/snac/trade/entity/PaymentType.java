package com.ureca.snac.trade.entity;

public enum PaymentType {
    POINT,  // 무료 재화
    MONEY   // 유료 재화 (판매자 정산은 항상 MONEY)
}