package com.ureca.snac.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@EnableRabbit
@Profile("!scheduler")
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


    /* ------------------- Direct : 거래 취소 전용 ------------------- */
    public static final String CANCEL_EXCHANGE = "cancel_exchange";
    public static final String CANCEL_QUEUE = "cancel_queue";
    public static final String CANCEL_ROUTING_KEY = "trade.cancel";

    @Bean
    public DirectExchange cancelExchange() {
        return new DirectExchange(CANCEL_EXCHANGE);
    }

    @Bean
    public Queue cancelQueue() {
        return new Queue(CANCEL_QUEUE, false);
    }

    @Bean
    public Binding cancelBinding(DirectExchange cancelExchange, Queue cancelQueue) {
        return BindingBuilder.bind(cancelQueue)
                .to(cancelExchange)
                .with(CANCEL_ROUTING_KEY);
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


    // 이메일 전송용 Direct Exchange
    public static final String EMAIL_EXCHANGE = "email_exchange";
    public static final String EMAIL_QUEUE = "email_queue";
    public static final String EMAIL_ROUTING_KEY = "email.send";

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, false);
    }

    @Bean
    public Binding emailBinding(DirectExchange emailExchange, Queue emailQueue) {
        return BindingBuilder
                .bind(emailQueue)
                .to(emailExchange)
                .with(EMAIL_ROUTING_KEY);
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

    // --- 신고 ---
    public static final String DISPUTE_NOTIFICATION_EXCHANGE = "dispute_notification_exchange";
    public static final String DISPUTE_NOTIFICATION_QUEUE = "dispute_notification_queue";
    public static final String DISPUTE_NOTIFICATION_ROUTING_KEY_PATTERN = "dispute.notification.#";

    @Bean
    public TopicExchange disputeNotificationExchange() {
        return new TopicExchange(DISPUTE_NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Queue disputeNotificationQueue() {
        return new Queue(DISPUTE_NOTIFICATION_QUEUE, false);
    }

    @Bean
    public Binding disputeNotificationBinding(TopicExchange disputeNotificationExchange, Queue disputeNotificationQueue) {
        return BindingBuilder
                .bind(disputeNotificationQueue)
                .to(disputeNotificationExchange)
                .with(DISPUTE_NOTIFICATION_ROUTING_KEY_PATTERN);
    }

    // 공통 비즈니스 익스체인지
    public static final String BUSINESS_EXCHANGE = "business_exchange";
    public static final String BUSINESS_DEAD_LETTER_EXCHANGE = "dlx.business";

    // 1) 회원가입 처리 전용 큐/라우팅키
    public static final String MEMBER_JOIN_QUEUE = "business.member.join.queue";
    public static final String MEMBER_JOIN_ROUTING_KEY = "business.member.join";
    public static final String MEMBER_JOIN_DLQ = "dlq.business.member.join";

    @Bean
    public DirectExchange businessExchange() {
        return new DirectExchange(BUSINESS_EXCHANGE);
    }

    @Bean
    public FanoutExchange businessDeadLetterExchange() {
        return new FanoutExchange(BUSINESS_DEAD_LETTER_EXCHANGE);
    }

    // 회원가입 전용 큐
    @Bean
    public Queue memberJoinDlq() {
        return new Queue(MEMBER_JOIN_DLQ);
    }

    @Bean
    public Binding memberJoinDlqBinding(FanoutExchange businessDeadLetterExchange, Queue memberJoinDlq) {
        return BindingBuilder
                .bind(memberJoinDlq)
                .to(businessDeadLetterExchange);
    }

    @Bean
    public Queue memberJoinQueue() {
        return QueueBuilder.durable(MEMBER_JOIN_QUEUE)
                .withArgument("x-dead-letter-exchange",
                        BUSINESS_DEAD_LETTER_EXCHANGE)
                .build();
    }

    @Bean
    public Binding memberJoinBinding(DirectExchange businessExchange, Queue memberJoinQueue) {
        return BindingBuilder
                .bind(memberJoinQueue)
                .to(businessExchange)
                .with(MEMBER_JOIN_ROUTING_KEY);
    }

    // 2) 자산 변경 처리 전용 큐/라우팅키
    public static final String ASSET_HISTORY_QUEUE = "business.asset.history.queue";
    public static final String ASSET_HISTORY_ROUTING_KEY = "business.asset.history";
    public static final String ASSET_HISTORY_DLQ = "dlq.business.asset.history";

    // 자산 변경 전용 큐
    @Bean
    public Queue assetHistoryDlq() {
        return new Queue(ASSET_HISTORY_DLQ);
    }

    @Bean
    public Binding assetHistoryDlqBinding(FanoutExchange businessDeadLetterExchange, Queue assetHistoryDlq) {
        return BindingBuilder
                .bind(assetHistoryDlq)
                .to(businessDeadLetterExchange);
    }

    @Bean
    public Queue assetHistoryQueue() {
        return QueueBuilder.durable(ASSET_HISTORY_QUEUE)
                .withArgument("x-dead-letter-exchange", BUSINESS_DEAD_LETTER_EXCHANGE)
                .build();
    }

    @Bean
    public Binding assetHistoryBinding(DirectExchange businessExchange, Queue assetHistoryQueue) {
        return BindingBuilder
                .bind(assetHistoryQueue)
                .to(businessExchange)
                .with(ASSET_HISTORY_ROUTING_KEY);
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

        RetryOperationsInterceptor retryInterceptor = RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(
                        1000,   // 초기 대기시간 1초
                        2.0,    // multiplier
                        10000   // 최대 대기시간 10초
                )
                .recoverer(new RejectAndDontRequeueRecoverer())  // 마지막엔 requeue 하지 않고 버림
                .build();

        factory.setAdviceChain(retryInterceptor);

        return factory;
    }
}
