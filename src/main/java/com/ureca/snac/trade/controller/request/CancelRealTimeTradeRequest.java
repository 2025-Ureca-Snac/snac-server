package com.ureca.snac.trade.controller.request;

import com.ureca.snac.trade.entity.CancelReason;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CancelRealTimeTradeRequest {
    private Long tradeId;
    private CancelReason reason;
}
