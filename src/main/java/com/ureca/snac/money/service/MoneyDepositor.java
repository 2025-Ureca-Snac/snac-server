package com.ureca.snac.money.service;

import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetChangedEventFactory;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
import com.ureca.snac.infra.dto.response.TossConfirmResponse;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.money.entity.MoneyRecharge;
import com.ureca.snac.money.repository.MoneyRechargeRepository;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.repository.PaymentRepository;
import com.ureca.snac.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoneyDepositor {

    private final PaymentRepository paymentRepository;
    private final MoneyRechargeRepository moneyRechargeRepository;
    private final WalletService walletService;
    private final AssetHistoryEventPublisher assetHistoryEventPublisher;
    private final AssetChangedEventFactory assetChangedEventFactory;

    @Transactional
    public void deposit(Payment payment, Member member, TossConfirmResponse tossConfirmResponse) {
        log.info("[머니 입금 처리] DB 상태 변경 시작. paymentId : {}", payment.getId());

        // 여기서 트랜잭션 미아가 발생해서 영속 상태로 되돌린다.
        Payment managedPayment = paymentRepository.save(payment);
        log.info("[머니 입금 처리] Payment 객체를 영속 상태로 전환");

        // 영속 객체를 사용하여 모든 작업 수행
        managedPayment.complete(tossConfirmResponse.paymentKey(),
                tossConfirmResponse.method(), tossConfirmResponse.approvedAt()
        );
        log.info("[머니 입금 처리] Payment 엔티티 SUCCESS 변경 완료");

        MoneyRecharge recharge = MoneyRecharge.create(managedPayment);
        moneyRechargeRepository.save(recharge);
        log.info("[머니 입금 처리] MoneyRecharge 기록 생성 완료. rechargeId : {}", recharge.getId());

        Long balanceAfter = walletService.depositMoney(member.getId(), payment.getAmount());
        log.info("[머니 입금 처리] 지갑 머니 입금 완료. member Id : {} , 최종 잔액 : {}",
                member.getId(), balanceAfter);

        AssetChangedEvent event = assetChangedEventFactory.createForRechargeEvent(
                member.getId(), recharge.getId(), recharge.getPaidAmountWon(), balanceAfter
        );
        assetHistoryEventPublisher.publish(event);
        log.info("[머니 입금 처리] 자산 변동 기록 이벤트 발행 완료.");
    }
}
