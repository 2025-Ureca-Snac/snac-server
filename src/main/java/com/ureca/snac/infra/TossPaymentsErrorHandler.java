package com.ureca.snac.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.infra.dto.TossErrorResponse;
import com.ureca.snac.payment.exception.PaymentRedirectException;
import com.ureca.snac.payment.exception.TossPaymentsAPiCallException;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

import static com.ureca.snac.common.BaseCode.ALREADY_PROCESSED_PAYMENT;

/**
 * 토스 페이먼츠 API 호출시 발생하는 HTTP 에러 처리
 * API를 호출하는 CLIENT 코드는 에러 처리 로직으로부터 분리
 */
public class TossPaymentsErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() ||
                response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(final URI url, final HttpMethod method, final ClientHttpResponse response) throws IOException {
        TossErrorResponse errorResponse =
                objectMapper.readValue(response.getBody(), TossErrorResponse.class);

        if ("ALREADY_PROCESSED_PAYMENT".equals(errorResponse.code())) {
            throw new PaymentRedirectException(ALREADY_PROCESSED_PAYMENT);
        }
        
        String detailedErrorMessage = String.format(
                "토스페이먼츠 결제 승인 실패 : %s (코드 : %s)",
                errorResponse.message(),
                errorResponse.code()
        );

        throw new TossPaymentsAPiCallException(detailedErrorMessage);
    }
}
