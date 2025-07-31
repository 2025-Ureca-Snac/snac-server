package com.ureca.snac.trade.dto.dispute;

import com.ureca.snac.trade.entity.TradeStatus;
import com.ureca.snac.trade.entity.TradeType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TradeSummaryDto {
    private Long tradeId;
    private TradeStatus status;
    private TradeType tradeType;
    private Integer priceGb;
    private Integer dataAmount;
    private String carrier;
    private String myRole;             // "BUYER" or "SELLER"
    private Long counterpartyId;       // 상대방 id
}
