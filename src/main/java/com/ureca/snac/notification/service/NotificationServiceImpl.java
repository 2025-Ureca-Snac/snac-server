package com.ureca.snac.notification.service;

import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.config.RabbitMQConfig;
import com.ureca.snac.notification.dto.NotificationDTO;
import com.ureca.snac.notification.entity.Notification;
import com.ureca.snac.notification.repository.NotificationRepository;
import com.ureca.snac.trade.dto.TradeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ureca.snac.config.RabbitMQConfig.*;
import static com.ureca.snac.config.RabbitMQConfig.MATCHING_NOTIFICATION_EXCHANGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE = NOTIFICATION_EXCHANGE;
    private static final String RK_FMT = "%s.%s";

    private static final String MATCHING_EXCHANGE = MATCHING_NOTIFICATION_EXCHANGE;

    @Override
    public void notify(String username, TradeDto tradeDto) {
        log.info("MQ 발행: {} {}", username, tradeDto);
        String routingKey = String.format("notification.%s", username);
        rabbitTemplate.convertAndSend(NOTIFICATION_EXCHANGE, routingKey, tradeDto);
    }

    @Override
    public void sendMatchingNotification(String username, CardDto cardDto) {
        log.info("매칭알림 MQ 발행: {} {}", username, cardDto);
        String routingKey = String.format("matching.notification.%s", username);
        rabbitTemplate.convertAndSend(MATCHING_NOTIFICATION_EXCHANGE, routingKey, cardDto);
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }
}
