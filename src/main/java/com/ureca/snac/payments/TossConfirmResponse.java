package com.ureca.snac.payments;

import java.time.LocalDateTime;

public record TossConfirmResponse(
        String paymentKey,
        String method,
        LocalDateTime approvedAt) {
}
