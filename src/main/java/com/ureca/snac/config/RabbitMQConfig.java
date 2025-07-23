package com.ureca.snac.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableRabbit
@Configuration
public class RabbitMQConfig {


    /* ------------------- Topic : 실시간 서비스 전용 ------------------- */
    public static final String NOTIFICATION_EXCHANGE = "notification_exchange";
    public static final String NOTIFICATION_QUEUE = "notification_queue";
    public static final String ROUTING_KEY_PATTERN = "notification.#";

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


    /* ------------------- Topic : 매칭 전용 ------------------- */
    public static final String MATCHING_NOTIFICATION_EXCHANGE = "matching_notification_exchange";
    public static final String MATCHING_NOTIFICATION_QUEUE = "matching_notification_queue";
    public static final String MATCHING_ROUTING_KEY_PATTERN = "matching.notification.#";

    @Bean
    public TopicExchange matchingNotificationExchange() {
        return new TopicExchange(MATCHING_NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue matchingNotificationQueue() {
        return new Queue(MATCHING_NOTIFICATION_QUEUE, false);
    }

    @Bean
    public Binding matchingNotificationBinding(TopicExchange matchingNotificationExchange,
                                               Queue matchingNotificationQueue) {
        return BindingBuilder
                .bind(matchingNotificationQueue)
                .to(matchingNotificationExchange)
                .with(MATCHING_ROUTING_KEY_PATTERN);
    }


    /* ------------------- Fanout : 전체 브로드캐스트용(공지, 이벤트 등) ------------------- */
    public static final String BROADCAST_EXCHANGE = "broadcast_exchange";
    public static final String BROADCAST_QUEUE = "broadcast_queue";

    @Bean
    public FanoutExchange broadcastExchange() {
        return new FanoutExchange(BROADCAST_EXCHANGE);
    }

    @Bean
    public Queue broadcastQueue() {
        return new Queue(BROADCAST_QUEUE, false);
    }

    @Bean
    public Binding broadcastBinding(FanoutExchange broadcastExchange, Queue broadcastQueue) {
        return BindingBuilder
                .bind(broadcastQueue)
                .to(broadcastExchange);
    }


    /* ------------------- Fanout : 접속자 수 전용 브로드캐스트 ------------------- */
    public static final String CONNECTED_USERS_EXCHANGE = "connected_users_exchange";
    public static final String CONNECTED_USERS_QUEUE = "connected_users_queue";

    @Bean
    public FanoutExchange connectedUsersExchange() {
        return new FanoutExchange(CONNECTED_USERS_EXCHANGE);
    }

    @Bean
    public Queue connectedUsersQueue() {
        return new Queue(CONNECTED_USERS_QUEUE, false);
    }

    @Bean
    public Binding connectedUsersBinding(FanoutExchange connectedUsersExchange, Queue connectedUsersQueue) {
        return BindingBuilder
                .bind(connectedUsersQueue)
                .to(connectedUsersExchange);
    }


    /* ------------------- Direct : SMS 전용 ------------------- */
    public static final String SMS_EXCHANGE = "sms_exchange";

    // 거래·알림 문자
    public static final String SMS_TRADE_QUEUE = "sms_trade_queue";
    public static final String SMS_TRADE_ROUTING_KEY = "sms.trade";

    // 인증 문자
    public static final String SMS_AUTH_QUEUE = "sms_auth_queue";
    public static final String SMS_AUTH_ROUTING_KEY = "sms.auth";

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

    /* ------------------- Direct : 카드 목록 조회용 ------------------- */
    public static final String CARD_LIST_EXCHANGE = "card_list_exchange";
    public static final String CARD_LIST_QUEUE = "card_list_queue";
    public static final String CARD_LIST_ROUTING_KEY = "card.list";

    @Bean
    public DirectExchange cardListExchange() {
        return new DirectExchange(CARD_LIST_EXCHANGE);
    }

    @Bean
    public Queue cardListQueue() {
        return new Queue(CARD_LIST_QUEUE, false);
    }

    @Bean
    public Binding cardListBinding(DirectExchange cardListExchange, Queue cardListQueue) {
        return BindingBuilder.bind(cardListQueue)
                .to(cardListExchange)
                .with(CARD_LIST_ROUTING_KEY);
    }

    /* ------------------- Direct : 필터 조회용 ------------------- */
    public static final String FILTER_EXCHANGE = "filter_exchange";
    public static final String FILTER_QUEUE = "filter_queue";
    public static final String FILTER_ROUTING_KEY = "filter.retrieve";

    @Bean
    public DirectExchange filterExchange() {
        return new DirectExchange(FILTER_EXCHANGE);
    }

    @Bean
    public Queue filterQueue() {
        return new Queue(FILTER_QUEUE, false);
    }

    @Bean
    public Binding filterBinding(DirectExchange filterExchange, Queue filterQueue) {
        return BindingBuilder.bind(filterQueue)
                .to(filterExchange)
                .with(FILTER_ROUTING_KEY);
    }


    /* ------------------- Direct : 에러 조회용 ------------------- */
    public static final String ERROR_EXCHANGE = "error_exchange";
    public static final String ERROR_QUEUE = "error_queue";
    public static final String ERROR_ROUTING_KEY = "error.socket";

    @Bean
    public DirectExchange errorExchange() {
        return new DirectExchange(ERROR_EXCHANGE);
    }

    @Bean
    public Queue errorQueue() {
        return new Queue(ERROR_QUEUE, false);
    }

    @Bean
    public Binding errorBinding(DirectExchange errorExchange, Queue errorQueue) {
        return BindingBuilder.bind(errorQueue)
                .to(errorExchange)
                .with(ERROR_ROUTING_KEY);
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
