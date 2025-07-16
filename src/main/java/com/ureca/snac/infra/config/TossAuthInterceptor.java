package com.ureca.snac.infra.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RequiredArgsConstructor
public class TossAuthInterceptor implements ClientHttpRequestInterceptor {

    private final String secretKey;

    @Override
    @NonNull
    public ClientHttpResponse intercept(
            @NonNull HttpRequest request,
            @NonNull byte[] body,
            @NonNull ClientHttpRequestExecution execution)
            throws IOException {
        byte[] encodedKey = Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8));

        String authHeaderValue = "Basic " + new String(encodedKey);
        request.getHeaders().set("Authorization", authHeaderValue);

        return execution.execute(request, body);
    }
}