package com.ureca.snac.payment.mapper;

import com.ureca.snac.common.exception.ExternalApiException;
import com.ureca.snac.infra.dto.response.TossCancelResponse;
import com.ureca.snac.payment.dto.PaymentCancelResponse;
import org.springframework.stereotype.Component;

import static com.ureca.snac.common.BaseCode.TOSS_API_CALL_ERROR;

/**
 * Mapper 패턴
 * 토스의 응답 DTO 를 우리 시스템 내부의 표준 DTO 변환
 */
@Component
public class PaymentCancelMapper {
    /**
     * 토스의 취소응답 객체를 paymentCancelResponse로 변환한다.
     *
     * @param tossCancelResponse 토스로 부터 받은 원본 응답 객체
     * @return 우리 시스템의 표준 응답 DTO로 바꿈
     */
    public PaymentCancelResponse toPaymentCancelResponse(TossCancelResponse tossCancelResponse) {
        // Toss 응답이 비정상적이거나 취소내역이 없는 경우
        if (tossCancelResponse == null || tossCancelResponse.cancels() == null ||
                tossCancelResponse.cancels().isEmpty()) {
            throw new ExternalApiException(TOSS_API_CALL_ERROR);
        }

        TossCancelResponse.Cancel firstCancel = tossCancelResponse.cancels().get(0);

        return PaymentCancelResponse.builder()
                .paymentKey(tossCancelResponse.paymentKey())
                .canceledAmount(firstCancel.cancelAmount())
                .cancelAt(firstCancel.canceledAt())
                .reason(firstCancel.cancelReason())
                .build();
    }
}
