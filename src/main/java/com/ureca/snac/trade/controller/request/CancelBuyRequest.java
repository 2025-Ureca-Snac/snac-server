package com.ureca.snac.trade.controller.request;

import com.ureca.snac.trade.entity.CancelReason;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelBuyRequest {
    private Long cardId;
    private CancelReason reason;
}
