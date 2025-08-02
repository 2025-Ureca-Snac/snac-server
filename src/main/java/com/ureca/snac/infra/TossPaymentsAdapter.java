package com.ureca.snac.infra;

import com.ureca.snac.infra.dto.response.TossCancelResponse;
import com.ureca.snac.infra.dto.response.TossConfirmResponse;
import com.ureca.snac.payment.dto.PaymentCancelResponse;
import com.ureca.snac.payment.mapper.PaymentCancelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * 어댑터 토스페이먼츠 구현체
 */
@Primary
@Component
@RequiredArgsConstructor
public class TossPaymentsAdapter implements PaymentGatewayAdapter {

    // 외부 통신
    private final TossPaymentsClient tossPaymentsClient;
    // DTO 매퍼
    private final PaymentCancelMapper paymentCancelMapper;

    @Override
    public TossConfirmResponse confirmPayment(String paymentKey, String orderId, Long amount) {
        // client에 위임
        return tossPaymentsClient.confirmPayment(paymentKey, orderId, amount);
    }

    @Override
    public PaymentCancelResponse cancelPayment(String paymentKey, String reason) {
        // 외부 통신에게 결제 취소 요청
        TossCancelResponse tossResponse = tossPaymentsClient.cancelPayment(paymentKey, reason);

        // 응답을 매퍼에 전달해서 서비스 DTO로 변경
        return paymentCancelMapper.toPaymentCancelResponse(tossResponse);
    }
}
