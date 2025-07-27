package com.ureca.snac.board.entity.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PriceRange {

    ALL     (null),
    P0_1000 (1000),
    P0_1500 (1500),
    P0_2000 (2000),
    P0_2500 (2500);

    private final Integer max;
}
