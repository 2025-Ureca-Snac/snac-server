package com.ureca.snac.settlement.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementStatus {
    SUCCESS("성공"),
    FAILED("실패");

    private final String displayName;
}
