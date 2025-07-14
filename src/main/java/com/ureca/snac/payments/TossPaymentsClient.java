package com.ureca.snac.payments;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * PaymentConfig가 연결해준 API 통신 전담 클래스
 * RestClient, Properties 를 주입 받고 API 명세
 * 에러 처리, 설정 관리, 책임 분리
 */

@RequiredArgsConstructor
public class TossPaymentsClient {

    private final RestClient restClient;
    private final TossPaymentProperties properties;

    /**
     * 결제  승인 API 를 호출하여 최종적으로 결제 완료
     *
     * @param paymentKey 토스페이먼츠가 발근하는 키
     * @param orderId    우리 시스템의 주문번호
     * @param amount     결제 금액
     * @return 결제 승인 후 토스페이먼츠로 받은 응답 정보를 담은 TossConfirmResponse 객체
     */
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