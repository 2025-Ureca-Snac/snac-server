package com.ureca.snac.payments;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * PaymentConfig가 연결해준 API 통신 전담 클래스
 */

@RequiredArgsConstructor
public class TossPaymentsClient {

    private final RestClient restClient;
    private final TossPaymentProperties properties;

    public TossConfirmResponse confirmPayment(String paymentKey, String orderId, Long amount) {

        return restClient.post()
                .uri(properties.getConfirmUrl())
                .headers(httpHeaders -> {
                    httpHeaders.set("Authorization", "Basic " +
                            properties.getEncodedSecretKey());
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                })
                .body(Map.of(
                        "paymentKey", paymentKey,
                        "orderId", orderId,
                        "amount", amount))
                .retrieve()
                .body(TossConfirmResponse.class);
    }
}