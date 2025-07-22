package com.ureca.snac.infra.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Toss Payments API 취소 응답 매핑
 * 내부용 Payment랑 구분해야됨
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TossCancelResponse(
        String paymentKey,
        String orderId,
        List<Cancel> cancels
) {
    public record Cancel(
            Long cancelAmount,
            String cancelReason,
            OffsetDateTime canceledAt
    ) {
    }
}
