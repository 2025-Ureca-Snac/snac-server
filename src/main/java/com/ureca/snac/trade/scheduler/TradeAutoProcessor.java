package com.ureca.snac.trade.scheduler;

import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetChangedEventFactory;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
import com.ureca.snac.board.entity.Card;
import com.ureca.snac.board.exception.CardNotFoundException;
import com.ureca.snac.board.repository.CardRepository;
import com.ureca.snac.member.Activated;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.trade.entity.PenaltyReason;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
import com.ureca.snac.trade.repository.TradeCancelRepository;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.PenaltyService;
import com.ureca.snac.wallet.service.WalletService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class TradeAutoProcessor {

    private final TradeRepository tradeRepo;
    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;
    private final PenaltyService penaltyService;

    private final TradeCancelRepository cancelRepo;
    // 기록 이벤트
    private final WalletService walletService;
    private final AssetHistoryEventPublisher assetHistoryEventPublisher;
    private final AssetChangedEventFactory assetChangedEventFactory;

    /**
     * 판매자가 48 시간 내 전송 안 한 거래 자동 환불
     */
    @Scheduled(cron = "0 0 * * * *")       // 매 정시
    @Transactional
    public void refundIfSellerNoSend() {
        LocalDateTime limit = LocalDateTime.now().minus(48, ChronoUnit.HOURS);

        List<Trade> trades = tradeRepo
                .findByStatusAndUpdatedAtBefore(TradeStatus.PAYMENT_CONFIRMED, limit);

        trades.forEach(trade -> {
            Card card = findLockedCard(trade.getCardId());
            Member buyer = trade.getBuyer();

            long moneyToRefund = trade.getPriceGb() - trade.getPoint();

            if (moneyToRefund > 0) {
                long moneyFinalBalance = walletService.depositMoney(buyer.getId(),
                        moneyToRefund);

                String title = String.format("%s %dGB 자동 환불",
                        card.getCarrier().name(), card.getDataAmount());

                AssetChangedEvent event = assetChangedEventFactory.createForTradeCancelWithMoney(
                        buyer.getId(), trade.getId(), title,
                        moneyToRefund, moneyFinalBalance
                );
                assetHistoryEventPublisher.publish(event);
            }

            long pointToRefund = trade.getPoint();
            if (pointToRefund > 0) {
                long pointFinalBalance = walletService.depositPoint(
                        buyer.getId(), pointToRefund
                );

                String title = String.format("%s %dGB 자동 포인트 환불",
                        card.getCarrier().name(), card.getDataAmount());

                AssetChangedEvent event =
                        assetChangedEventFactory.createForTradeCancelWithPoint(
                                buyer.getId(), trade.getId(), title,
                                pointToRefund, pointFinalBalance
                        );
                assetHistoryEventPublisher.publish(event);
            }
//            // 환불
//            Wallet buyerWallet = tradeSupport.findLockedWallet(trade.getBuyer().getId());
//            buyerWallet.depositMoney((long) (trade.getPriceGb() - trade.getPoint()) * trade.getDataAmount());
//            buyerWallet.depositPoint((long) trade.getPoint() * trade.getDataAmount());

            // 상태 변경
            trade.cancel(trade.getBuyer());

            // 패널티 (판매자 지연)
            penaltyService.givePenalty(trade.getSeller().getEmail(), PenaltyReason.AUTO_DELAY);

            log.info("[AUTO_REFUND] trade {} 환불 완료", trade.getId());
        });
    }

    /**
     * 구매자가 48 시간 내 확정 안 한 거래 자동 정산
     */
    @Scheduled(cron = "0 30 * * * *")      // 매시 30분
    @Transactional
    public void payoutIfBuyerNoConfirm() {
        LocalDateTime limit = LocalDateTime.now().minus(48, ChronoUnit.HOURS);

        List<Trade> trades = tradeRepo
                .findByStatusAndUpdatedAtBeforeAndAutoConfirmPausedFalse(TradeStatus.DATA_SENT, limit);

        trades.forEach(trade -> {
            Card card = findLockedCard(trade.getCardId());
            Member seller = trade.getSeller();

            // 1. 판매 대금 정상
            long amountToDeposit = trade.getPriceGb();
            long finalBalance = walletService.depositMoney(seller.getId(), amountToDeposit);

            String title = String.format("%s %dGB 자동 정산",
                    card.getCarrier().name(), card.getDataAmount());
            AssetChangedEvent event = assetChangedEventFactory.createForSell(
                    seller.getId(), trade.getId(), title, amountToDeposit, finalBalance
            );
            assetHistoryEventPublisher.publish(event);

//            // 정산
//            Wallet sellerWallet = tradeSupport.findLockedWallet(trade.getSeller().getId());
//            sellerWallet.depositMoney((long) trade.getPriceGb() * trade.getDataAmount());

            // 상태 변경
            trade.changeStatus(TradeStatus.COMPLETED);

            // 패널티 (구매자 지연)
            penaltyService.givePenalty(trade.getBuyer().getEmail(), PenaltyReason.AUTO_DELAY);

            log.info("[AUTO_PAYOUT] trade {} 판매자 정산 완료", trade.getId());
        });
    }

    @Scheduled(cron = "0 0 0 * * *")  // 매일 00:00
    @Transactional
    public void liftExpiredSuspensions() {
        List<Member> members = memberRepository.findByActivatedAndSuspendUntilBefore(
                Activated.TEMP_SUSPEND, LocalDateTime.now());
        members.forEach(Member::activate);
    }

    private Card findLockedCard(Long cardId) {
        return cardRepository.findLockedById(cardId).orElseThrow(CardNotFoundException::new);
    }
}
