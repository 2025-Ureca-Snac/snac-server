package com.ureca.snac.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.common.exception.ExternalApiException;
import com.ureca.snac.infra.dto.response.TossErrorResponse;
import com.ureca.snac.payment.exception.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static com.ureca.snac.common.BaseCode.TOSS_API_CALL_ERROR;

/**
 * 토스 페이먼츠 API 호출시 발생하는 HTTP 에러 처리
 * API를 호출하는 CLIENT 코드는 에러 처리 로직으로부터 분리
 */
@Slf4j
public class TossPaymentsErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() ||
                response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(@NonNull final URI url,
                            @NonNull final HttpMethod method,
                            final ClientHttpResponse response) throws IOException {
        String responseBody = new String(response.getBody().readAllBytes(),
                StandardCharsets.UTF_8);

        log.error("[외부 API 에러] TOss API 호출 실패 Status : {}, URL : {}, responseBody : {}",
                response.getStatusCode(), url, responseBody);

        TossErrorResponse errorResponse;
        try {
            errorResponse = objectMapper.readValue(responseBody, TossErrorResponse.class);

        } catch (JsonProcessingException e) {
            throw new TossPaymentsAPiCallException("토스페이먼츠 API의 에러 응답을 파싱 실패" + responseBody);
        }

        TossErrorCode errorCode = TossErrorCode.fromCode(errorResponse.code());

        switch (errorCode) {
            case INVALID_CARD_EXPIRATION:
            case INVALID_CARD_NUMBER:
            case REJECT_CARD_COMPANY:
                throw new TossInvalidCardInfoException();

            case NOT_ENOUGH_BALANCE:
                throw new TossNotEnoughBalanceException();

            case INVALID_API_KEY:
            case UNAUTHORIZED_KEY:
            case INVALID_AUTHORIZATION:
                throw new TossInvalidApiKeyException();

            case ALREADY_PROCESSED_PAYMENT:
                throw new PaymentAlreadyProcessedPaymentException();

            default:
                String detailedErrorMessage = String.format(
                        "처리되지않은 토스 API 에러 발생 : %s (코드 : %s)",
                        errorResponse.message(),
                        errorResponse.code()
                );
                throw new ExternalApiException(
                        TOSS_API_CALL_ERROR, detailedErrorMessage);
        }
    }
}
