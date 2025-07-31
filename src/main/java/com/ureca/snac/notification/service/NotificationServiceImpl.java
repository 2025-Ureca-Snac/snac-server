package com.ureca.snac.notification.service;

import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.notification.repository.NotificationRepository;
import com.ureca.snac.trade.dto.CancelTradeDto;
import com.ureca.snac.trade.dto.RetrieveFilterDto;
import com.ureca.snac.trade.dto.TradeDto;
import com.ureca.snac.trade.dto.dispute.DisputeNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import static com.ureca.snac.config.RabbitMQConfig.*;

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

    @Override
    public void sendBuyFilterNotification(RetrieveFilterDto dto) {
        log.info("[필터 발행] username={} filterCount={}", dto.getUsername(), dto.getBuyerFilter().size());
        rabbitTemplate.convertAndSend(FILTER_EXCHANGE, FILTER_ROUTING_KEY, dto);
    }

    @Override
    public void sendCancelNotification(CancelTradeDto dto) {
        log.info("[거래 취소 발행] username={} tradeId={}", dto.getUsername(), dto.getTradeDto().getTradeId());
        rabbitTemplate.convertAndSend(CANCEL_EXCHANGE, CANCEL_ROUTING_KEY, dto);
    }

    @Override
    public void sendDisputeNotification(String username, DisputeNotificationDto dto) {
        log.info("[신고 알림 발행] username={}, dto={}", username, dto);
        String routingKey = String.format("dispute.notification.%s", username);
        rabbitTemplate.convertAndSend(DISPUTE_NOTIFICATION_EXCHANGE, routingKey, dto);
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }
}
