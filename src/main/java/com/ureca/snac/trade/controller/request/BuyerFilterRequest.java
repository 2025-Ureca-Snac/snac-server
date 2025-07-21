package com.ureca.snac.trade.controller.request;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.PriceRange;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BuyerFilterRequest {
    private Carrier carrier;
    private Integer dataAmount;
    private PriceRange priceRange;
}
