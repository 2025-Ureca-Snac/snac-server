package com.ureca.snac.payments;

import com.ureca.snac.payments.dto.TossConfirmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TossPaymentsClient {
    private final WebClient webClient;

    @Value("${payments.toss.secret-key}")
    private String secretKey;

    @Value("${payments.toss.confirm-url}")
    private String confirmUrl;


    public TossConfirmResponse confirmPayment(String paymentKey, String orderId, Long amount) {
        String encodedSecretKey = Base64.getEncoder().
                encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        return webClient.post()
                .uri(confirmUrl)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedSecretKey)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(Map.of("paymentKey", paymentKey, "orderId", orderId, "amount", amount))
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(new RuntimeException("토스페이먼츠 결제 승인 실패: "
                                        + body)))
                ).bodyToMono(TossConfirmResponse.class)
                .block();
    }
}
