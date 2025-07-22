package com.ureca.snac.trade.dto;

import com.ureca.snac.trade.entity.CancelReason;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelTradeRequest {
    @NotNull
    private CancelReason reason;
}