package com.ureca.snac.asset.service;

import com.ureca.snac.asset.entity.AssetType;
import com.ureca.snac.asset.entity.SourceDomain;
import com.ureca.snac.asset.entity.TransactionCategory;
import com.ureca.snac.asset.entity.TransactionType;
import com.ureca.snac.asset.event.AssetChangedEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 비즈니스 로직에서 발생하는 이벤트 객체를 생성하는 팩토리 클래스로
 * 이벤트 생성 로직을 중앙에서 관리해서 코드 중복을 제거함
 * 그러면 서비스 레이어는 언제 이벤트를 발생시킬지만 결정하면 된다.
 */
@Component
public class AssetChangedEventFactory {

    /**
     * 전략 패턴의 ENUM 구현체
     * 각 AssetType, TransactionType, TransactionCategory 을 조합해서
     * 하나의 거래 유형 전략 으로 정의
     */
    @Getter
    @RequiredArgsConstructor
    private enum TradeEventType {
        BUY_WITH_MONEY(
                AssetType.MONEY,
                TransactionType.WITHDRAWAL,
                TransactionCategory.BUY
        ),
        BUY_WITH_POINT(
                AssetType.POINT,
                TransactionType.WITHDRAWAL,
                TransactionCategory.POINT_USAGE
        ),
        SELL_TO_MONEY(
                AssetType.MONEY,
                TransactionType.DEPOSIT,
                TransactionCategory.SELL
        ),
        CANCEL_MONEY_REFUND(
                AssetType.MONEY,
                TransactionType.DEPOSIT,
                TransactionCategory.CANCEL
        ),
        CANCEL_POINT_REFUND(
                AssetType.POINT,
                TransactionType.DEPOSIT,
                TransactionCategory.CANCEL
        );

        private final AssetType assetType;
        private final TransactionType transactionType;
        private final TransactionCategory category;
    }

    /**
     * 거래 관련 이벤트 생성 헬퍼 메소드
     * TradeEventType만 선택해서 전달하면됨 전략 패턴사용
     *
     * @param eventType    Enum 전략
     * @param memberId     이벤트를 발생 시킨 회원 ID
     * @param tradeId      출처가 되는 충전 기록 ID
     * @param title        기록될 제목
     * @param amount       변동된 금액
     * @param balanceAfter 최종 잔액
     * @return 생성된 자산 변동 이벤트
     */
    private AssetChangedEvent createTradeEvent(
            TradeEventType eventType, Long memberId, Long tradeId,
            String title, Long amount, Long balanceAfter) {

        return AssetChangedEvent.builder()
                .memberId(memberId)
                .assetType(eventType.getAssetType())
                .transactionType(eventType.getTransactionType())
                .category(eventType.getCategory())
                .amount(amount)
                .balanceAfter(balanceAfter)
                .title(title)
                .sourceDomain(SourceDomain.TRADE)
                .sourceId(tradeId)
                .build();
    }

    /**
     * 전략 패턴의 ENUM 구현체
     * 각 AssetType, TransactionType, TransactionCategory 을 조합하고
     * title과 SourceDomain 까지 합쳐서 한다.
     * 하나의 충전 유형 전략 으로 정의
     */
    @Getter
    @RequiredArgsConstructor
    private enum RechargeEventType {
        RECHARGE(
                AssetType.MONEY,
                TransactionType.DEPOSIT,
                TransactionCategory.RECHARGE,
                "%,d원 충전",
                SourceDomain.PAYMENT
        ),
        CANCEL(
                AssetType.MONEY,
                TransactionType.WITHDRAWAL,
                TransactionCategory.CANCEL,
                "%,d원 충전 취소",
                SourceDomain.PAYMENT
        );

        private final AssetType assetType;
        private final TransactionType transactionType;
        private final TransactionCategory category;
        private final String titleFormat;
        private final SourceDomain sourceDomain;
    }

    private AssetChangedEvent createForRechargeEvent(
            RechargeEventType rechargeEventType, Long memberId, Long sourceId,
            Long amount, Long balanceAfter) {
        return AssetChangedEvent.builder()
                .memberId(memberId)
                .assetType(rechargeEventType.getAssetType())
                .transactionType(rechargeEventType.getTransactionType())
                .category(rechargeEventType.getCategory())
                .amount(amount)
                .balanceAfter(balanceAfter)
                .title(String.format(rechargeEventType.getTitleFormat(), amount))
                .sourceDomain(rechargeEventType.getSourceDomain())
                .sourceId(sourceId)
                .build();
    }

    // 거래관련
    public AssetChangedEvent createForBuyWithMoney(
            Long memberId, Long tradeId,
            String title, Long amount, Long balanceAfter) {
        return createTradeEvent(TradeEventType.BUY_WITH_MONEY,
                memberId, tradeId, title, amount, balanceAfter);

    }

    public AssetChangedEvent createForBuyWithPoint(
            Long memberId, Long tradeId,
            String title, Long amount, Long balanceAfter) {
        return createTradeEvent(TradeEventType.BUY_WITH_POINT,
                memberId, tradeId, title, amount, balanceAfter);

    }

    public AssetChangedEvent createForSell(
            Long memberId, Long tradeId,
            String title, Long amount, Long balanceAfter) {
        return createTradeEvent(TradeEventType.SELL_TO_MONEY,
                memberId, tradeId, title, amount, balanceAfter);

    }

    public AssetChangedEvent createForTradeCancelWithMoney(
            Long memberId, Long tradeId,
            String title, Long amount, Long balanceAfter) {
        return createTradeEvent(TradeEventType.CANCEL_MONEY_REFUND,
                memberId, tradeId, title, amount, balanceAfter);

    }

    public AssetChangedEvent createForTradeCancelWithPoint(
            Long memberId, Long tradeId,
            String title, Long amount, Long balanceAfter) {
        return createTradeEvent(TradeEventType.CANCEL_POINT_REFUND,
                memberId, tradeId, title, amount, balanceAfter);

    }

    // 충전 관련
    public AssetChangedEvent createForRechargeEvent(
            Long memberId, Long paymentId,
            Long amount, Long balanceAfter) {
        return createForRechargeEvent(RechargeEventType.RECHARGE,
                memberId, paymentId, amount, balanceAfter);

    }

    public AssetChangedEvent createForRechargeCancel(
            Long memberId, Long paymentId,
            Long amount, Long balanceAfter) {
        return createForRechargeEvent(RechargeEventType.CANCEL,
                memberId, paymentId, amount, balanceAfter);

    }

    // 정산 관련
    public AssetChangedEvent createForSettlement(
            Long memberId, Long settlementId,
            Long amount, Long balanceAfter) {

        return AssetChangedEvent.builder()
                .memberId(memberId)
                .assetType(AssetType.MONEY)
                .transactionType(TransactionType.WITHDRAWAL)
                .category(TransactionCategory.SETTLEMENT)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .title(String.format("%,d원 정산", amount))
                .sourceDomain(SourceDomain.SETTLEMENT)
                .sourceId(settlementId)
                .build();
    }

    public AssetChangedEvent createForSignupBonus(
            Long memberId, Long amount, Long balanceAfter) {
        return AssetChangedEvent.builder()
                .memberId(memberId)
                .assetType(AssetType.POINT)
                .transactionType(TransactionType.DEPOSIT)
                .category(TransactionCategory.EVENT)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .title("회원가입 축하 포인트")
                .sourceDomain(SourceDomain.EVENT)
                .sourceId(memberId)
                .build();
    }
}
