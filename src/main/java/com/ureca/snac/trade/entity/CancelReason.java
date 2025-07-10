package com.ureca.snac.trade.entity;

public enum CancelReason {
    BUYER_REQUEST,
    SELLER_REQUEST,
    OVERPAYMENT,
    TIMEOUT_CONFIRM,
    TIMEOUT_PAYMENT
}