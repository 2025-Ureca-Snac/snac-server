package com.ureca.snac.config;

import com.ureca.snac.infra.TossPaymentsErrorHandler;
import com.ureca.snac.infra.config.TossAuthInterceptor;
import com.ureca.snac.infra.config.TossPaymentProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
@RequiredArgsConstructor
public class RestClientConfig {

    private final TossPaymentProperties tossPaymentProperties;

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
    public RestClient tossRestClient(
            TossPaymentsErrorHandler errorHandler,
            ClientHttpRequestFactory clientHttpRequestFactory
    ) {
        return RestClient.builder()
                .baseUrl(tossPaymentProperties.getUrl())
                .requestInterceptor(new
                        TossAuthInterceptor(tossPaymentProperties.getSecretKey()))
                .requestFactory(clientHttpRequestFactory)
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