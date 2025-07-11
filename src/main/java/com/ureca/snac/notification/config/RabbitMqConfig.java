package com.ureca.snac.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// RabbitMQ 설정 클래스
//
public class RabbitMqConfig {
    public static final String EXCHANGE = "notification.exchange"; // 메시지를 전달하는 교환기 이름, 메시지를 전달 받아 라우팅 규칙에 따라 큐로 전달
    public static final String QUEUE = "notification.queue"; // 알림 메시지를 저장할 큐, 최종적으로 메시지가 저장되어 컨슈머가 읽어가는 버퍼
    public static final String ROUTING_KEY = "notification.key"; // 라우팅에 사용될 라우팅 키, exchange 와  queue 를 연결하고 라우팅 키로 어떤 메세지가 해당 큐로 가야하는지 정의
    // direct exchange 를 사용하므로 라우팅 키가 완전히 일치할 때만 메시지가 큐로 전달

    @Bean
    public Exchange exchange() {
        // durable=true 로 설정하여 서버 재시작 시에도 Exchange 가 유지
        return ExchangeBuilder.directExchange(EXCHANGE).durable(true).build();
    }
    @Bean
    public Queue queue() {
        // durable 큐 : 메시지가 큐에 남아있다가 컨슈머가 받을 때까지 보존
        return QueueBuilder.durable(QUEUE).build();
    }
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(queue()).to(exchange()).with(ROUTING_KEY).noargs();
    }
}