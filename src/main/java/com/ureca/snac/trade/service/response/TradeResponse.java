package com.ureca.snac.trade.service.response;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.trade.dto.TradeSide;
import com.ureca.snac.trade.entity.CancelReason;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
import com.ureca.snac.trade.entity.TradeType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TradeResponse {
    private Long tradeId;

    // 판매자 OR 구매자
    private String buyer;
    private String seller;

    private Integer priceGb;
    private Integer dataAmount;
    private String phone;

    private Carrier carrier;
    private CancelReason cancelReason;
    private TradeStatus status;
    private TradeType tradeType;

    private LocalDateTime createdAt;

    public static TradeResponse from(Trade trade, String username) {
        String phoneToShow = null;

        // 구매자 측면에서는 언제나 본인 번호를 보여주고,
        if (username.equals(trade.getBuyer().getEmail())) {
            phoneToShow = trade.getPhone();
        }
        // 판매자 측면에서는 결제 확정 단계(PAYMENT_CONFIRMED)일 때만 보여줌
        else if (username.equals(trade.getSeller().getEmail()) && trade.getStatus() == TradeStatus.PAYMENT_CONFIRMED) {
            phoneToShow = trade.getPhone();
        }

        return new TradeResponse(
                trade.getId(),

                trade.getBuyer().getEmail(),
                (trade.getSeller() != null) ? trade.getSeller().getEmail() : "",

                trade.getPriceGb(),
                trade.getDataAmount(),
                phoneToShow,
                trade.getCarrier(),
                trade.getCancelReason(),
                trade.getStatus(),
                trade.getTradeType(),
                trade.getCreatedAt()
        );
    }

    public static TradeResponse from(Trade trade, TradeSide side) {
        String phoneToShow = null;

        // 구매자 측면에서는 언제나 본인 번호를 보여주고,
        if (side == TradeSide.BUY) {
            phoneToShow = trade.getPhone();
        }
        // 판매자 측면에서는 결제 확정 단계(PAYMENT_CONFIRMED)일 때만 보여줌
        else if (side == TradeSide.SELL && trade.getStatus() == TradeStatus.PAYMENT_CONFIRMED) {
            phoneToShow = trade.getPhone();
        }

        return new TradeResponse(
                trade.getId(),
                trade.getBuyer().getEmail(),
                (trade.getSeller() != null) ? trade.getSeller().getEmail() : "",
                trade.getPriceGb(),
                trade.getDataAmount(),
                phoneToShow,
                trade.getCarrier(),
                trade.getCancelReason(),
                trade.getStatus(),
                trade.getTradeType(),
                trade.getCreatedAt()
        );
    }
}
