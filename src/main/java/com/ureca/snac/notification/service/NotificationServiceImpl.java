package com.ureca.snac.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.member.Member;
import com.ureca.snac.notification.config.RabbitMqConfig;
import com.ureca.snac.notification.dto.NotificationMessage;
import com.ureca.snac.notification.entity.Notification;
import com.ureca.snac.notification.entity.NotificationType;
import com.ureca.snac.notification.repository.NotificationRepository;
import com.ureca.snac.trade.entity.Trade;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;
    private final AmqpTemplate amqp;
    private final ObjectMapper mapper;

    @Override
    @Transactional
    // 데이터 일관성을 위해 db 저장과 mq 하나의 트랜잭션으로
    // but mq 발행 실패는 사용자 알림만 발생하고 서비스 전체 롤백은 필요 없으므로
    // catch 블록에서 예외를 기록하고 롤백 x
    public void notify(Member from, Member to, NotificationType type, Trade trade) {
        // DB 저장
        Notification n = Notification.builder()
                .memberFrom(from)
                .memberTo(to)
                .type(type)
                .isRead(false)
                .build();
        repo.save(n);

        // RabbitMQ 로 알림 메시지 발행
        NotificationMessage msg = new NotificationMessage(to.getId(), type, trade.getId());
        try {
            amqp.convertAndSend(RabbitMqConfig.EXCHANGE,
                                RabbitMqConfig.ROUTING_KEY,
                                mapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            e.printStackTrace();   // 서비스 흐름은 유지
        }
    }
}