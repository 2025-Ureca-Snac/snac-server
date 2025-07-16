package com.ureca.snac.infra.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossConfirmRequest(
        String paymentKey,
        String orderId,
        Long amount
) {
}
