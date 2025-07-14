package com.ureca.snac.payments;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

/**
 * 토스 페이먼츠 API 호출시 발생하는 HTTP 에러 처리
 * API를 호출하는 CLIENT 코드는 에러 처리 로직으로부터 분리
 */
public class TossPaymentsErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(final URI url, final HttpMethod method, final ClientHttpResponse response) throws IOException {
        TossErrorResponse errorResponse =
                objectMapper.readValue(response.getBody(), TossErrorResponse.class);
        String detailedErrorMessage = String.format(
                "토스페이먼츠 결제 승인 실패 : %s (코드 : %s)",
                errorResponse.message(),
                errorResponse.code()
        );

        throw new RuntimeException(detailedErrorMessage);
    }
}
