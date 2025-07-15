package com.ureca.snac.config;

import com.ureca.snac.infra.TossPaymentProperties;
import com.ureca.snac.infra.TossPaymentsClient;
import com.ureca.snac.infra.TossPaymentsErrorHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
public class PaymentConfig {
    @Bean
    public TossPaymentsErrorHandler tossPaymentsErrorHandler() {
        return new TossPaymentsErrorHandler();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(10000);

        return factory;
    }

    @Bean
    public TossPaymentsClient tossRestClient(
            TossPaymentProperties properties,
            TossPaymentsErrorHandler errorHandler,
            ClientHttpRequestFactory clientHttpRequestFactory
    ) {

        RestClient tossRestClient = RestClient.builder()
                .requestFactory(clientHttpRequestFactory)
                .defaultHeader("Authorization", "Basic " +
                        properties.getEncodedSecretKey())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultStatusHandler(errorHandler)
                .build();

        return new TossPaymentsClient(tossRestClient, properties);
    }
}

/*
 * RestTemplate가 구식이라고 하는 이유로 WebClient 추천
 * 그런데 굳이 비동기를 안쓸꺼라면 쓸 이유를 모르겠다
 * RestClient 동기에다가 더 최신 HTTP 클라이언트
 * 의존성도 필요없음
 */