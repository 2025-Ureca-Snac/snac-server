package com.ureca.snac.infra;

import com.ureca.snac.infra.dto.TossConfirmRequest;
import com.ureca.snac.infra.dto.TossConfirmResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

/**
 * PaymentConfig가 연결해준 API 통신 전담 클래스
 * RestClient, Properties 를 주입 받고 API 명세
 * 에러 처리, 설정 관리, 책임 분리
 */

@Slf4j
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

        log.info("[외부 API] 토스 페이먼츠 결제 승인 API 호출 시작. 주문번호 : {}", orderId);

        // Map -> DTO로 개선
        TossConfirmRequest request = new TossConfirmRequest(paymentKey, orderId, amount);

        return restClient.post()
                .uri(properties.getConfirmUrl())
                .body(request)
                .retrieve()
                .body(TossConfirmResponse.class);
    }
}