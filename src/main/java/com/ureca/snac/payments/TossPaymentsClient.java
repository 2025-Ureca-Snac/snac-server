package com.ureca.snac.payments;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossPaymentsClient {
    private final RestClient restClient;

    @Value("${payments.toss.secret-key}")
    private String secretKey;

    @Value("${payments.toss.confirm-url}")
    private String confirmUrl;


    public TossConfirmResponse confirmPayment(String paymentKey, String orderId, Long amount) {
        String encodedSecretKey = Base64.getEncoder().
                encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        ResponseEntity<TossConfirmResponse> responseEntity =
                restClient.post()
                        .uri(confirmUrl)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedSecretKey)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(Map.of("paymentKey", paymentKey, "orderId", orderId, "amount", amount))
                        .retrieve()
                        .onStatus(
                                status -> status.is4xxClientError() || status.is5xxServerError(),
                                (request, response) -> {
                                    throw new RuntimeException("토스페이먼츠 결제 승인 실패: " + response.getStatusText());
                                })
                        .toEntity(TossConfirmResponse.class);
        return responseEntity.getBody();
    }
}
