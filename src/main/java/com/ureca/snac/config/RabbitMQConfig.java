package com.ureca.snac.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    /* ------------------- Topic : 실시간 매칭 ------------------- */
    public static final String NOTIFICATION_EXCHANGE = "notification_exchange";
    public static final String NOTIFICATION_QUEUE    = "notification_queue";
    public static final String ROUTING_KEY_PATTERN   = "notification.#";

    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, false);
    }

    @Bean
    public Binding notificationBinding(TopicExchange notificationExchange, Queue notificationQueue) {
        return BindingBuilder
                .bind(notificationQueue)
                .to(notificationExchange)
                .with(ROUTING_KEY_PATTERN);
    }

    /* ------------------- Direct : SMS 전용 ------------------- */
    public static final String SMS_EXCHANGE     = "sms_exchange";

    // 거래·알림 문자
    public static final String SMS_TRADE_QUEUE       = "sms_trade_queue";
    public static final String SMS_TRADE_ROUTING_KEY = "sms.trade";

    // 인증 문자
    public static final String SMS_AUTH_QUEUE        = "sms_auth_queue";
    public static final String SMS_AUTH_ROUTING_KEY  = "sms.auth";

    @Bean
    public DirectExchange smsExchange() {
        return new DirectExchange(SMS_EXCHANGE);
    }

    @Bean
    public Queue smsTradeQueue() {  // 거래 전용 큐
        return new Queue(SMS_TRADE_QUEUE, false);
    }

    @Bean
    public Queue smsAuthQueue() {   // 인증 전용 큐
        return new Queue(SMS_AUTH_QUEUE, false);
    }

    @Bean
    public Binding smsTradeBinding(DirectExchange smsExchange, Queue smsTradeQueue) {
        return BindingBuilder.bind(smsTradeQueue)
                .to(smsExchange)
                .with(SMS_TRADE_ROUTING_KEY);
    }

    @Bean
    public Binding smsAuthBinding(DirectExchange smsExchange, Queue smsAuthQueue) {
        return BindingBuilder.bind(smsAuthQueue)
                .to(smsExchange)
                .with(SMS_AUTH_ROUTING_KEY);
    }

    /* ------------------- 공통 설정 ------------------- */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }
}
