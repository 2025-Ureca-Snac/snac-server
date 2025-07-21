package com.ureca.snac.asset.service;

import com.ureca.snac.asset.entity.AssetType;
import com.ureca.snac.asset.entity.SourceDomain;
import com.ureca.snac.asset.entity.TransactionCategory;
import com.ureca.snac.asset.entity.TransactionType;
import com.ureca.snac.asset.event.AssetChangedEvent;
import org.springframework.stereotype.Component;

/**
 * 비즈니스 로직에서 발생하는 이벤트 객체를 생성하는 팩토리 클래스로
 * 이벤트 생성 로직을 중앙에서 관리해서 코드 중복을 제거함
 * 그러면 서비스 레이어는 언제 이벤트를 발생시킬지만 결정하면 된다.
 */
@Component
public class AssetChangedEventFactory {
    /**
     * 머니 충전 성공시의 이벤트 생성
     *
     * @param memberId     이벤트를 발생 시킨 회원 ID
     * @param rechargeId   출처가 되는 충전 기록 ID
     * @param amount       변동된 그액
     * @param balanceAfter 거래 후 최종 잔액
     * @return 자산 변동 이벤트
     */
    public AssetChangedEvent createForRecharge(
            Long memberId, Long rechargeId, Long amount, Long balanceAfter) {
        return AssetChangedEvent.builder()
                .memberId(memberId)
                .assetType(AssetType.MONEY)
                .transactionType(TransactionType.DEPOSIT)
                .category(TransactionCategory.RECHARGE)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .title("스낵 머니 충전")
                .sourceDomain(SourceDomain.MONEY_RECHARGE)
                .sourceId(rechargeId)
                .build();
    }

    /**
     * 머니 충전 취소 성공시의 이벤트 생성
     *
     * @param memberId     이벤트를 발생 시킨 회원 ID
     * @param paymentId    출처가 되는 결제 기록 ID
     * @param amount       변동된 금액
     * @param balanceAfter 거래후 최종 잔액
     * @return 자산 변동 이벤트
     */
    public AssetChangedEvent createForCancel(
            Long memberId, Long paymentId, Long amount, Long balanceAfter) {
        return AssetChangedEvent.builder()
                .memberId(memberId)
                .assetType(AssetType.MONEY)
                .transactionType(TransactionType.WITHDRAWAL)
                .category(TransactionCategory.CANCEL)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .title("스낵 머니 충전 취소")
                .sourceDomain(SourceDomain.PAYMENT)
                .sourceId(paymentId)
                .build();
    }

    /**
     * 거래에 있어서 머니로 구매했을 때의 이벤트
     *
     * @param memberId     구매자 회원 ID
     * @param tradeId      거래 기록 ID
     * @param title        거래 내역에 표시될 제목 SKT 2GB 머니 사용
     * @param amount       사용된 머니 금액
     * @param balanceAfter 거래후 최종 머니 잔액
     * @return 생성된 자산 변동 이벤트
     */
    public AssetChangedEvent createForBuyWithMoney(
            Long memberId, Long tradeId, String title, Long amount, Long balanceAfter) {
        return AssetChangedEvent.builder()
                .memberId(memberId)
                .assetType(AssetType.MONEY)
                .transactionType(TransactionType.WITHDRAWAL)
                .category(TransactionCategory.BUY)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .title(title)
                .sourceDomain(SourceDomain.TRADE)
                .sourceId(tradeId)
                .build();
    }

    /**
     * 거래에 있어서 포인트로 구매했을 때의 이벤트
     *
     * @param memberId     구매자 회원 ID
     * @param tradeId      거래 기록 ID
     * @param title        거래 내역에 표시될 제목 SKT 2GB 포인트 사용
     * @param amount       사용된 포인트 금액
     * @param balanceAfter 거래후 최종 포인트 잔액
     * @return 생성된 자산 변동 이벤트
     */
    public AssetChangedEvent createForBuyWithPoint(
            Long memberId, Long tradeId, String title, Long amount, Long balanceAfter) {
        return AssetChangedEvent.builder()
                .memberId(memberId)
                .assetType(AssetType.POINT)
                .transactionType(TransactionType.WITHDRAWAL)
                .category(TransactionCategory.POINT_USAGE)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .title(title)
                .sourceDomain(SourceDomain.TRADE)
                .sourceId(tradeId)
                .build();
    }

    /**
     * 거래에 있어서 판매해서 머니로 정산받는 이벤트
     *
     * @param memberId     판매자 회원 ID
     * @param tradeId      거래 기록 ID
     * @param title        거래 내역에 표시될 제목 SKT 2GB 판매 대금
     * @param amount       정산받은 머니 금액
     * @param balanceAfter 거래후 최종 머니 잔액
     * @return 생성된 자산 변동 이벤트
     */
    public AssetChangedEvent createForSell(
            Long memberId, Long tradeId, String title, Long amount, Long balanceAfter) {
        return AssetChangedEvent.builder()
                .memberId(memberId)
                .assetType(AssetType.MONEY)
                .transactionType(TransactionType.DEPOSIT)
                .category(TransactionCategory.SELL)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .title(title)
                .sourceDomain(SourceDomain.TRADE)
                .sourceId(tradeId)
                .build();
    }
}
