package com.ureca.snac.infra.dto.request;

public record TossConfirmRequest(
        String paymentKey,
        String orderId,
        Long amount
) {
}
