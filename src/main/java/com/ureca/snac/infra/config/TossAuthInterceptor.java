package com.ureca.snac.infra.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TossAuthInterceptor implements ClientHttpRequestInterceptor {

    /**
     * Authorization 헤더값
     */
    private final String preCalculatedAuthHeader;

    /**
     * 생성자로 부터 시크릿 키 받아서 Base64 인코딩하고
     * 인증 헤더값 한번만 계산 걍 빈 생성 처럼
     *
     * @param secretKey 시크릿 키
     */
    public TossAuthInterceptor(String secretKey) {
        byte[] encodedKey = Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        this.preCalculatedAuthHeader = "Basic " + new String(encodedKey);
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(
            @NonNull HttpRequest request,
            @NonNull byte[] body,
            @NonNull ClientHttpRequestExecution execution)
            throws IOException {

        // 미리 계산된 헤더 값
        request.getHeaders().set("Authorization", this.preCalculatedAuthHeader);
        return execution.execute(request, body);
    }
}