package com.ureca.snac.asset.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 자산의 종류 정의 머니랑 포인트
 */
@Getter
@RequiredArgsConstructor
public enum AssetType {
    MONEY("머니"),
    POINT("포인트");

    private final String displayName;
}
