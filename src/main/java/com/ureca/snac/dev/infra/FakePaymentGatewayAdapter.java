package com.ureca.snac.dev.infra;

import com.ureca.snac.infra.PaymentGatewayAdapter;
import com.ureca.snac.infra.dto.response.TossConfirmResponse;
import com.ureca.snac.payment.dto.PaymentCancelResponse;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.exception.PaymentNotFoundException;
import com.ureca.snac.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Profile("!prod")
@Slf4j
@Component
@RequiredArgsConstructor
public class FakePaymentGatewayAdapter implements PaymentGatewayAdapter {

    private final PaymentRepository paymentRepository;

    @Override
    public TossConfirmResponse confirmPayment(String paymentKey, String orderId, Long amount) {
        log.warn("[Fake Adapter] 실제 API 호출 없이 더미 응답");
        return new TossConfirmResponse(
                paymentKey,
                "개발용 충전",
                OffsetDateTime.now()
        );
    }

    @Override
    public PaymentCancelResponse cancelPayment(String paymentKey, String reason) {
        log.warn("[Fake Adapter] 실제 API 호출 없이 더미응답");

        Payment payment = paymentRepository.findByPaymentKeyWithMember(paymentKey)
                .orElseThrow(PaymentNotFoundException::new);

        return PaymentCancelResponse.builder()
                .paymentKey(paymentKey)
                .canceledAmount(payment.getAmount())
                .cancelAt(OffsetDateTime.now())
                .reason(reason)
                .build();
    }
}
