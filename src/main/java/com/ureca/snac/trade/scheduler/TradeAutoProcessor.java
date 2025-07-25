package com.ureca.snac.trade.scheduler;

import com.ureca.snac.member.Activated;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.trade.entity.PenaltyReason;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
import com.ureca.snac.trade.repository.TradeCancelRepository;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.PenaltyService;
import com.ureca.snac.trade.support.TradeSupport;
import com.ureca.snac.wallet.entity.Wallet;
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
    private final TradeSupport tradeSupport;
    private final MemberRepository memberRepository;
    private final PenaltyService penaltyService;
    private final TradeCancelRepository cancelRepo;

    /** 판매자가 48 시간 내 전송 안 한 거래 자동 환불 */
    @Scheduled(cron = "0 0 * * * *")       // 매 정시
    @Transactional
    public void refundIfSellerNoSend() {
        LocalDateTime limit = LocalDateTime.now().minus(48, ChronoUnit.HOURS);

        List<Trade> trades = tradeRepo
                .findByStatusAndUpdatedAtBefore(TradeStatus.PAYMENT_CONFIRMED, limit);

        trades.forEach(trade -> {
            // 환불
            Wallet buyerWallet = tradeSupport.findLockedWallet(trade.getBuyer().getId());
            buyerWallet.depositMoney((long) (trade.getPriceGb() - trade.getPoint()) * trade.getDataAmount());
            buyerWallet.depositPoint((long) trade.getPoint() * trade.getDataAmount());

            // 상태 변경
            trade.cancel(trade.getBuyer());

            // 패널티 (판매자 지연)
            penaltyService.givePenalty(trade.getSeller().getEmail(), PenaltyReason.AUTO_DELAY);

            log.info("[AUTO_REFUND] trade {} 환불 완료", trade.getId());
        });
    }

    /** 구매자가 48 시간 내 확정 안 한 거래 자동 정산 */
    @Scheduled(cron = "0 30 * * * *")      // 매시 30분
    @Transactional
    public void payoutIfBuyerNoConfirm() {
        LocalDateTime limit = LocalDateTime.now().minus(48, ChronoUnit.HOURS);

        List<Trade> trades = tradeRepo
                .findByStatusAndUpdatedAtBeforeAndAutoConfirmPausedFalse(TradeStatus.DATA_SENT, limit);

        trades.forEach(trade -> {
            // 정산
            Wallet sellerWallet = tradeSupport.findLockedWallet(trade.getSeller().getId());
            sellerWallet.depositMoney((long) trade.getPriceGb() * trade.getDataAmount());

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
}