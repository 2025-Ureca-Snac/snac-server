package com.ureca.snac.payment.mapper;

import com.ureca.snac.common.exception.ExternalApiException;
import com.ureca.snac.infra.dto.response.TossCancelResponse;
import com.ureca.snac.payment.dto.PaymentCancelResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentCancelMapperTest {
    private PaymentCancelMapper paymentCancelMapper;

    @BeforeEach
    void setUp() {
        paymentCancelMapper = new PaymentCancelMapper();
    }

    @Test
    void 토스_API_취소_응답을_내부_DTO로_변환_성공() {
        // given
        OffsetDateTime cancelAt = OffsetDateTime.now();
        TossCancelResponse.Cancel cancelDetail = new TossCancelResponse.Cancel(
                10000L,
                "고객 변심",
                cancelAt
        );

        TossCancelResponse tossCancelResponse = new TossCancelResponse(
                "test_payment_key",
                "test_orderId",
                List.of(cancelDetail)
        );

        // when
        PaymentCancelResponse response = paymentCancelMapper.toPaymentCancelResponse(tossCancelResponse);

        // then
        assertThat(response).isNotNull();
        assertThat(response.paymentKey()).isEqualTo("test_payment_key");
        assertThat(response.canceledAmount()).isEqualTo(10000L);
        assertThat(response.cancelAt()).isEqualTo(cancelAt);
        assertThat(response.reason()).isEqualTo("고객 변심");
    }

    @Test
    void 토스_API_응답이_null_이면_예외_발생() {
        // given
        TossCancelResponse tossCancelResponse = null;

        // when then
        assertThrows(ExternalApiException.class,
                () -> paymentCancelMapper.toPaymentCancelResponse(tossCancelResponse));
    }

    @Test
    void 토스_API_응답의_취소내역이_비어있으면_예외_발생() {
        // given
        TossCancelResponse tossCancelResponse = new TossCancelResponse(
                "test_payment_key",
                "test_order_id",
                Collections.emptyList()
        );
        
        // when then
        assertThrows(ExternalApiException.class,
                () -> paymentCancelMapper.toPaymentCancelResponse(tossCancelResponse));
    }
}