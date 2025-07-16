package com.ureca.snac.trade.service.response;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.entity.CancelReason;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TradeResponse {
    private Long tradeId;

    private Integer priceGb;
    private Integer dataAmount;
    private String phone;

    private Carrier carrier;
    private CancelReason cancelReason;
    private TradeStatus status;

    private LocalDateTime createdAt;

    public static TradeResponse from(Trade trade, TradeSide side) {
        String maskedPhone = (side == TradeSide.SELL) ? null : trade.getPhone();

        return new TradeResponse(
                trade.getId(),
                trade.getPriceGb(),
                trade.getDataAmount(),
                maskedPhone,
                trade.getCarrier(),
                trade.getCancelReason(),
                trade.getStatus(),
                trade.getCreatedAt()
        );
    }
}
