package com.ureca.snac.trade.service.response;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.trade.entity.*;
import com.ureca.snac.trade.repository.TradeCancelRepository;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TradeResponse {
    private final Long tradeId;

    // 판매자 OR 구매자
    private final String buyer;
    private final String seller;

    private final Integer priceGb;
    private final Integer dataAmount;
    private final String phone;

    private final Carrier carrier;
    private final CancelReason cancelReason;
    private final TradeStatus status;
    private final TradeType tradeType;

    private final LocalDateTime createdAt;

    // 대기 중인 취소요청 표시용
    private final boolean cancelRequested; // 취소요청 대기중?
    private final CancelReason cancelRequestReason; // 취소 요청 사유
    private final CancelStatus cancelRequestStatus;

    private final Long partnerId; // 상대방 ID
    private final String partnerNickname; // 상대방 닉네임
    private final boolean isPartnerFavorite; // 거래 상대방이 나랑 단골인지?

    private TradeResponse(Long tradeId, String buyer, String seller, Integer priceGb,
                          Integer dataAmount, String phone, Carrier carrier, CancelReason cancelReason,
                          TradeStatus status, TradeType tradeType, LocalDateTime createdAt,
                          boolean cancelRequested, CancelReason cancelRequestReason,
                          CancelStatus cancelRequestStatus,
                          Long partnerId, String partnerNickname, boolean isPartnerFavorite) {
        this.tradeId = tradeId;
        this.buyer = buyer;
        this.seller = seller;
        this.priceGb = priceGb;
        this.dataAmount = dataAmount;
        this.phone = phone;
        this.carrier = carrier;
        this.cancelReason = cancelReason;
        this.status = status;
        this.tradeType = tradeType;
        this.createdAt = createdAt;
        this.cancelRequested = cancelRequested;
        this.cancelRequestReason = cancelRequestReason;
        this.cancelRequestStatus = cancelRequestStatus;
        this.partnerId = partnerId;
        this.partnerNickname = partnerNickname;
        this.isPartnerFavorite = isPartnerFavorite;
    }

    public static TradeResponse from(
            Trade trade, String username, boolean isPartnerFavorite,
            TradeCancelRepository.TradeCancelSummary cancel, Long partnerId,
            String partnerNickname) {

        String phoneToShow = null;

        if (username.equals(trade.getBuyer().getEmail())) {
            phoneToShow = trade.getPhone();
        } else if (trade.getSeller() != null &&
                username.equals(trade.getSeller().getEmail()) &&
                trade.getStatus() == TradeStatus.PAYMENT_CONFIRMED) {
            phoneToShow = trade.getPhone();
        }

        boolean isCancelRequest = cancel != null;
        CancelReason reason = isCancelRequest ? cancel.getReason() : null;
        CancelStatus status = isCancelRequest ? cancel.getStatus() : null;

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
                trade.getCreatedAt(),
                isCancelRequest,
                reason,
                status,
                partnerId,
                partnerNickname,
                isPartnerFavorite
        );
    }

    public static TradeResponse from(Trade trade, String username) {
        return from(trade, username, false, null, null, null);
    }
}
