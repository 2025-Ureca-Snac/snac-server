package com.ureca.snac.payment.service;

import com.ureca.snac.member.Member;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;

    @Override
    @Transactional
    public Payment preparePayment(Member member, Long amount) {
        log.info("[결제 준비] 시작. 회원 ID: {}, 요청 금액 : {}", member.getId(), amount);

        // Payment 객체 생성하고
        Payment payment = Payment.prepare(member, amount);

        // 디비에 저장
        Payment savedPayment = paymentRepository.save(payment);
        log.info("[결제 준비] PENDING 상태 결제 생성 주문번호 : {}", savedPayment.getOrderId());

        return savedPayment;
    }
}
