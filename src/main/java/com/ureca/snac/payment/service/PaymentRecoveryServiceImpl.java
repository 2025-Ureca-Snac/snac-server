package com.ureca.snac.payment.service;

import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRecoveryServiceImpl implements PaymentRecoveryService {

    private final PaymentRepository paymentRepository;

    @Override
    public void processInternalFailure(Payment payment, Exception e) {
        try {
            log.warn("[재난 복구] 시작. Payment ID : {}", payment.getId());

            // 준영속 상태 방지로 다시 조회
            Payment newPayment = paymentRepository.findById(payment.getId())
                    .orElse(payment);
            newPayment.recordFailure("INTERNAL_DB_ERROR", e.getMessage());
            paymentRepository.save(newPayment);

            log.warn("[재난 복구] 완료. Payment 상태를 FAIL로 기록 Payment ID : {}", newPayment.getId());
        } catch (Exception exception) {

            // 기록마저 실패하는 최악의 경우 로그만이라도 남겨야된다.

            log.error("[재난 복구 시스템 실패] FAIL 상태 기록 중에도 예외 발생" +
                    "데이터 불일치 발생 Payment ID : {}", payment.getId(), e);
        }
    }
}
