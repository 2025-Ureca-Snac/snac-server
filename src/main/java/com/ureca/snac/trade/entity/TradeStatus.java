package com.ureca.snac.trade.entity;

public enum TradeStatus {
    BUY_REQUESTED,
    SELL_REQUESTED,
    ACCEPTED, // 판매자 혹은 구매자가 수락
    PAYMENT_CONFIRMED, // 입금 확인됨
    DATA_SENT, // 데이터 전송 완료
    COMPLETED, // 거래 완료
    CANCELED // 거래 취소
}
