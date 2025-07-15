package com.ureca.snac.config;

import com.ureca.snac.infra.TossPaymentProperties;
import com.ureca.snac.infra.TossPaymentsErrorHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
public class PaymentConfig {
    @Bean
    public TossPaymentsErrorHandler tossPaymentsErrorHandler() {
        return new TossPaymentsErrorHandler();
    }

    @Bean
    public RestClient tossRestClient(
            TossPaymentProperties properties,
            TossPaymentsErrorHandler errorHandler
    ) {
        return RestClient.builder()
                .defaultHeader("Authorization", "Basic " +
                        properties.getEncodedSecretKey())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultStatusHandler(errorHandler)
                .build();
    }
}

/*
 * RestTemplate가 구식이라고 하는 이유로 WebClient 추천
 * 그런데 굳이 비동기를 안쓸꺼라면 쓸 이유를 모르겠다
 * RestClient 동기에다가 더 최신 HTTP 클라이언트
 * 의존성도 필요없음
 */