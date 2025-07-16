package com.ureca.snac.payment.service;

import com.ureca.snac.infra.TossPaymentsClient;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.exception.PaymentNotFoundException;
import com.ureca.snac.payment.exception.PaymentOwnershipMismatchException;
import com.ureca.snac.payment.repository.PaymentRepository;
import com.ureca.snac.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {

    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final WalletService walletService;
    private final TossPaymentsClient tossPaymentsClient;

    @Override
    @Transactional
    public Payment preparePayment(Member member, Long amount) {
        // Payment 객체 생성하고
        Payment payment = Payment.prepare(member, amount);

        // 디비에 저장
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public void cancelPayment(String paymentKey, String reason, String email) {
        log.info("[결제 취소] 시작. 결제 ID : {}", paymentKey);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                .orElseThrow(PaymentNotFoundException::new);

        // 결제 멤버랑 현재 요청자 검토
        if (payment.validateOwner(member)) {
            throw new PaymentOwnershipMismatchException();
        }

        tossPaymentsClient.cancelPayment(payment.getPaymentKey(), reason);

        // 캡슐화 서비스는 취소만 여부는 엔티티가
        payment.cancel(reason);

        walletService.withdrawMoney(member.getId(), payment.getAmount());
        log.info("[결제 취소] 최종 완료 결제 ID : {}", payment.getId());

    }
}
