package com.ureca.snac.trade.entity;

import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.board.entity.constants.SellStatus;
import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.Member;
import com.ureca.snac.trade.exception.*;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.ureca.snac.board.entity.constants.SellStatus.SELLING;
import static com.ureca.snac.trade.entity.TradeStatus.*;

@Getter
@Entity
@Table(name = "trade",
        uniqueConstraints = @UniqueConstraint(name = "uk_trade_card_member", columnNames = {"card_id", "buyer_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trade extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_id")
    private Long id;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Member seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    private Member buyer;

    @Enumerated(EnumType.STRING)
    @Column(name = "carrier", nullable = false)
    private Carrier carrier;

    @Column(name = "price_gb", nullable = false)
    private Integer priceGb; // 1기가 당 가격

    @Column(name = "data_amount", nullable = false)
    private Integer dataAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancel_reason")
    private CancelReason cancelReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TradeStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "trade_type", nullable = false)
    private TradeType tradeType;

    @Column(name = "point")
    private Integer point;

    @Column(name = "phone", nullable = false, length = 11)
    private String phone;

    // 문의접수 된 거래면 자동확정 x
    @Column(name = "auto_confirm_paused", nullable = false)
    private boolean autoConfirmPaused = false;

    @Builder
    private Trade(Long cardId, Member seller, Member buyer,
                  Carrier carrier, Integer priceGb, Integer dataAmount, TradeStatus status, TradeType tradeType, String phone, Integer point) {
        this.cardId = cardId;
        this.seller = seller;
        this.buyer = buyer;
        this.carrier = carrier;
        this.priceGb = priceGb;
        this.dataAmount = dataAmount;
        this.status = status;
        this.phone = phone;
        this.point = point;
        this.tradeType = tradeType;
    }

    public static Trade createFake(Card card, Member seller, Member buyer) {
        return Trade.builder()
                .tradeType(TradeType.NORMAL)
                .cardId(card.getId())
                .seller(seller)
                .buyer(buyer)
                .carrier(card.getCarrier())
                .priceGb(card.getPrice())
                .dataAmount(card.getDataAmount())
                .status(COMPLETED)
                .phone("01011111111")
                .point(0)
                .build();
    }

    // 거래 상태 변경
    public void changeStatus(TradeStatus status) {
        this.status = status;
    }

    // === 팩토리 메서드 ===
    public static Trade buildTrade(int point, Member member, String phone, Card card, SellStatus requiredStatus) {
        return Trade.builder().cardId(card.getId())
                .tradeType(TradeType.NORMAL)
                .seller(requiredStatus == SELLING ? card.getMember() : null)
                .buyer(member)
                .carrier(card.getCarrier())
                .priceGb(card.getPrice())
                .dataAmount(card.getDataAmount())
                .status(PAYMENT_CONFIRMED)
                .phone(phone)
                .point(point)
                .build();
    }

    public static Trade buildTrade(Member member, String phone, Card card) {
        return Trade.builder().cardId(card.getId())
                .tradeType(TradeType.REALTIME)
                .seller(card.getMember())
                .buyer(member)
                .carrier(card.getCarrier())
                .priceGb(card.getPrice())
                .dataAmount(card.getDataAmount())
                .status(BUY_REQUESTED)
                .phone(phone)
                .point(0)
                .build();
    }

    public void confirm(Member buyer) {
        // 거래 상태가 데이터 전송 완료 상태가 아니면 확정할 수 없음
        if (this.status != DATA_SENT)
            throw new TradeStatusMismatchException();

        // 요청자가 실제 구매자가 아니면 확정 권한이 없음
        if (this.buyer != buyer) {
            throw new TradeConfirmPermissionDeniedException();
        }

        // 거래 상태를 완료로 변경
        this.status = COMPLETED;
    }

    public void cancel(Member requester) {
        // 데이터 전송 이후, 완료되었거나 이미 취소된 거래는 취소 불가
        if (this.status == DATA_SENT || this.status == COMPLETED || this.status == CANCELED)
            throw new TradeCancelNotAllowedException();

        // 취소 요청자가 구매자 또는 판매자인지 확인
        boolean isBuyer = requester.equals(this.buyer);
        boolean isSeller = requester.equals(this.seller);

        // 거래 당사자가 아니라면 취소 권한 없음
        if (!isBuyer && !isSeller)
            throw new TradeCancelPermissionDeniedException();

        // 취소 요청자에 따라 취소 사유 지정
//        this.cancelReason = isBuyer ? (CancelReason.BUYER_CHANGE_MIND) : (CancelReason.SELLER_CHANGE_MIND);
        // 이 부분 TradeCancel 에서 저장

        // 거래 상태를 '취소됨'으로 변경
        this.status = CANCELED;
    }


    public void pauseAutoConfirm()  {
        this.autoConfirmPaused = true;
    }
    public void resumeAutoConfirm() {
        this.autoConfirmPaused = false;
    }

    public void changeCancelReason(CancelReason cancelReason) {
        this.cancelReason = cancelReason;
    }

    // 리팩토링
    // 현재 status가 expected가 아니면 예외
    public void ensureStatus(TradeStatus expected) {
        if (this.status != expected) {
            throw new TradeStatusMismatchException();
        }
    }

    // BUY_REQUESTED → ACCEPTED 전환
    public void accept(Member member) {
        ensureStatus(BUY_REQUESTED);
        ensureSeller(member);
        this.status = ACCEPTED;
    }

    // 현재 seller가 아니라면 예외
    public void ensureSeller(Member member) {
        if (this.seller == null || !this.seller.equals(member)) {
            throw new TradePermissionDeniedException();
        }
    }

    //요청자가 실제 거래의 구매자인지 검증, 아니라면 TradePermissionDeniedException을 던진다.
    public void ensureBuyer(Member member) {
        if (this.buyer == null || !this.buyer.equals(member)) {
            throw new TradePermissionDeniedException();
        }
    }

    // ACCEPTED -> PAYMENT_CONFIRMED 전환
    public void markPaymentConfirmed(int point) {
        ensureStatus(ACCEPTED);
        this.point = point;
        this.status = PAYMENT_CONFIRMED;
    }

    /**
     * seller 로직 검증 및 할당만 담당
     * - 본인이 요청자(구매자)와 같으면 예외
     */
    public void assignSeller(Member seller) {
        if (seller.equals(this.buyer)) {
            throw new TradeSelfRequestException();
        }
        this.seller = seller;
    }

    /**
     * 결제 금액(머니 + 포인트)이 주문 금액(priceGb)과 일치하는지 검증.
     * 일치하지 않으면 TradePaymentMismatchException을 던진다.
     */
    public void ensurePaymentMatches(long money, long point) {
        long expected = this.priceGb.longValue();
        long actual   = money + point;
        if (expected != actual) {
            throw new TradePaymentMismatchException();
        }
    }
}
