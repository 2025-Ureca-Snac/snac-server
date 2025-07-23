package com.ureca.snac.board.entity.constants;

public enum SellStatus {
    PENDING, // 구매글이 결제 전 일때
    SELLING, // 판매글 결제 전, 구매글 결제 후
    TRADING, // 거래중일때
    SOLD_OUT, // 판매완료
    CANCEL // 거래 취소되고 닫힘
}
