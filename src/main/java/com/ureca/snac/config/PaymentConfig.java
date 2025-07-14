package com.ureca.snac.config;

import com.ureca.snac.payments.TossPaymentProperties;
import com.ureca.snac.payments.TossPaymentsClient;
import com.ureca.snac.payments.TossPaymentsErrorHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
public class PaymentConfig {
    @Bean
    public TossPaymentsErrorHandler tossPaymentsErrorHandler() {
        return new TossPaymentsErrorHandler();
    }

    @Bean
    public TossPaymentsClient tossPaymentsClient(
            RestClient.Builder builder,
            TossPaymentProperties properties,
            TossPaymentsErrorHandler errorHandler
    ) {
        RestClient restClient = builder
                .defaultStatusHandler(errorHandler)
                .build();

        return new TossPaymentsClient(restClient, properties);
    }
}

/*
 * RestTemplate가 구식이라고 하는 이유로 WebClient 추천
 * 그런데 굳이 비동기를 안쓸꺼라면 쓸 이유를 모르겠다
 * RestClient 동기에다가 더 최신 HTTP 클라이언트
 * 의존성도 필요없음
 */