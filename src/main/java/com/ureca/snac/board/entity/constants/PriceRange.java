package com.ureca.snac.board.entity.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PriceRange {

    ALL        (null,   null),
    P0_999     (0,      999),
    P1000_1499 (1000,   1499),
    P1500_1999 (1500,   1999),
    P2000_2499 (2000,   2499),
    P2500_PLUS (2500,   null);

    private final Integer min;
    private final Integer max;
}
