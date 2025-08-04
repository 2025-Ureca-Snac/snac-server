package com.ureca.snac.infra;

import com.ureca.snac.infra.dto.response.TossCancelResponse;
import com.ureca.snac.infra.dto.response.TossConfirmResponse;
import com.ureca.snac.payment.dto.PaymentCancelResponse;
import com.ureca.snac.payment.mapper.PaymentCancelMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TossPaymentsAdapterTest {

    @InjectMocks
    private TossPaymentsAdapter tossPaymentsAdapter;

    @Mock
    private TossPaymentsClient tossPaymentsClient;

    @Mock
    private PaymentCancelMapper paymentCancelMapper;

    @Test
    void 결제_승인_요청시_Client_에게_위임() {
        // given
        String paymentKey = "test_payment_key";
        String orderId = "test_order_id";
        Long amount = 10000L;
        when(tossPaymentsClient.confirmPayment(paymentKey, orderId, amount))
                .thenReturn(new TossConfirmResponse(
                        "toss_key", "카드", OffsetDateTime.now()));

        // when
        tossPaymentsAdapter.confirmPayment(paymentKey, orderId, amount);

        // then
        verify(tossPaymentsClient).confirmPayment(paymentKey, orderId, amount);
    }

    @Test
    void 결제_취소_요청시_Client_에게_위임() {
        // given
        String paymentKey = "test_payment_key";
        String cancelReason = "고객 변심";

        TossCancelResponse tossCancelResponse = new TossCancelResponse
                ("key", "orderId", List.of());

        when(tossPaymentsClient.cancelPayment(paymentKey, cancelReason))
                .thenReturn(tossCancelResponse);

        PaymentCancelResponse internalResponse = new PaymentCancelResponse(
                paymentKey, 0L, OffsetDateTime.now(), cancelReason
        );

        when(paymentCancelMapper.toPaymentCancelResponse(any(TossCancelResponse.class)))
                .thenReturn(internalResponse);

        // when
        tossPaymentsAdapter.cancelPayment(paymentKey, cancelReason);

        // then
        verify(tossPaymentsClient).cancelPayment(paymentKey, cancelReason);
        verify(paymentCancelMapper).toPaymentCancelResponse(tossCancelResponse);
    }
}