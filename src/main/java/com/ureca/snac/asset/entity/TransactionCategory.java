package com.ureca.snac.asset.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 왜 돈이 움직였냐
 * 거래의 비즈니스 성격
 * 네이버 페이의 충전과 결제 태그 느낌
 */
@Getter
@RequiredArgsConstructor
public enum TransactionCategory {
    RECHARGE("충전"),
    BUY("구매"),
    SELL("판매"),
    CANCEL("취소"),
    EVENT("적립");

    private final String displayName;

    /**
     * 검증 로직
     * 자산 타입이랑 태그 종류 매칭하는 거
     * 예를 들어 포인트는 충전하거나 판매가 안된다.
     */
    public boolean isValidFor(AssetType assetType) {
        return switch (assetType) {
            case MONEY -> this == RECHARGE || this == CANCEL || this == BUY || this == SELL;
            case POINT -> this == EVENT || this == BUY;
        };
    }

    /**
     * 이 카테고리가 특정 거래 타입과 일관성이 있는지
     * 이벤트로 돈이 나가는건 안된다 이벤트는 적립만
     */
    public boolean isConsistentWith(TransactionType transactionType) {
        return switch (transactionType) {
            case DEPOSIT -> this == RECHARGE || this == SELL || this == EVENT;
            case WITHDRAWAL -> this == BUY || this == CANCEL;
        };
    }
}
