package com.ureca.snac.trade.dto;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.trade.entity.CancelReason;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TradeDto {
    private Long tradeId;
    private Long cardId;

    private Long sellerId;
    private String seller;
    private String sellerNickName;
    private Integer sellerRatingScore;

    private Long buyerId;
    private String buyer;
    private String buyerNickname;

    private Carrier carrier;
    private Integer priceGb;
    private Integer dataAmount;
    private CancelReason cancelReason;
    private TradeStatus status;
    private Integer point;
    private String phone;

    public static TradeDto from(Trade trade) {
        return TradeDto.builder()
                .tradeId(trade.getId())
                .cardId(trade.getCardId())

                .sellerId(trade.getSeller().getId())
                .seller(trade.getSeller().getEmail())
                .sellerNickName(trade.getSeller().getNickname())
                .sellerRatingScore(trade.getSeller().getRatingScore())

                .buyerId(trade.getBuyer().getId())
                .buyer(trade.getBuyer().getEmail())
                .buyerNickname(trade.getBuyer().getNickname())
                
                .carrier(trade.getCarrier())
                .priceGb(trade.getPriceGb())
                .dataAmount(trade.getDataAmount())
                .cancelReason(trade.getCancelReason())
                .status(trade.getStatus())
                .point(trade.getPoint())
                .phone(trade.getPhone())
                .build();
    }
}
