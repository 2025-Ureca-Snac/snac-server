package com.ureca.snac.payment.service;

import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.service.AssetChangedEventFactory;
import com.ureca.snac.asset.service.AssetHistoryEventPublisher;
import com.ureca.snac.member.Member;
import com.ureca.snac.payment.dto.PaymentCancelResponse;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.repository.PaymentRepository;
import com.ureca.snac.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [신규 컴포넌트]
 * 결제와 관련된 단일 책임 내부 DB 상태 변경
 * 내부적인 헬퍼 역할
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentInternalService {

    private final PaymentRepository paymentRepository;
    private final WalletService walletService;
    private final AssetHistoryEventPublisher assetHistoryEventPublisher;
    private final AssetChangedEventFactory assetChangedEventFactory;

    /**
     * 결제 취소에 따른 내부 DB 상태 변경 책임
     * 토스페이먼츠 결제 취소 성공 후 호출
     *
     * @param payment        상태 변경할 Payment 엔티티
     * @param cancelResponse 우리서비스의 응답 DTO
     */
    @Transactional
    public void processCancellationInDB(Payment payment, Member member, PaymentCancelResponse cancelResponse) {
        log.info("[내부 처리] 결제 취소 DB 상태 변경 시작 paymentId : {}", payment.getId());

        Payment managedPayment = paymentRepository.save(payment);
        log.info("[내부 처리] 준영속 상태의 Payment 객체를 영속성 전환");

        // 관리 상태가 된 객체 변경
        managedPayment.cancel(cancelResponse.reason());
        log.info("[내부 처리] Payment 엔티티 상태 CANCELED 상태");

        // 머니 잔액을 회수
        Long balanceAfter = walletService.depositMoney(member.getId(),
                managedPayment.getAmount());
        log.info("[내부 처리] 지갑 머니 회수(출금) 완료 회원 ID : {}, 최종 잔액 : {}",
                member.getId(), balanceAfter);

        AssetChangedEvent event = assetChangedEventFactory.createForRechargeCancel(
                member.getId(), managedPayment.getId(), managedPayment.getAmount(), balanceAfter
        );

        assetHistoryEventPublisher.publish(event);
        log.info("[내부 처리] 자산 변동 기록 이벤트 발행 완료. 회원 ID : {}", member.getId());
    }
}
