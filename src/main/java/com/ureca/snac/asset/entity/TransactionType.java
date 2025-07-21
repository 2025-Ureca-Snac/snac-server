package com.ureca.snac.asset.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 돈이 어떻게 움직였냐 라는 회계적 책임
 * 입금이랑 출금 + 랑 - 구분
 */
@Getter
@RequiredArgsConstructor
public enum TransactionType {
    DEPOSIT("입금/적립"), // 자산 증가 == 충전, 판매, 적립
    WITHDRAWAL("출금/사용"); // 자산 감소 == 충전 취소, 구매, 출금

    public final String description;
}
