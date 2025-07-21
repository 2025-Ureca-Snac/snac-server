package com.ureca.snac.infra.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.OffsetDateTime;
import java.util.List;

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
