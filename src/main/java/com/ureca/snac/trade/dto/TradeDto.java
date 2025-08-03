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
    private Integer buyerRatingScore;

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

                .sellerId(trade.getSeller() != null ? trade.getSeller().getId() : null)
                .seller(trade.getSeller() != null ? trade.getSeller().getEmail() : null)
                .sellerNickName(trade.getSeller() != null ? trade.getSeller().getNickname() : null)
                .sellerRatingScore(trade.getSeller() != null ? trade.getSeller().getRatingScore() : null)

                .buyerId(trade.getBuyer() != null ? trade.getBuyer().getId() : null)
                .buyer(trade.getBuyer() != null ? trade.getBuyer().getEmail() : null)
                .buyerNickname(trade.getBuyer() != null ? trade.getBuyer().getNickname() : null)
                .buyerRatingScore(trade.getBuyer() != null ? trade.getBuyer().getRatingScore() : null)

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
