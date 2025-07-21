package com.ureca.snac.notification.listener;

import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.config.RabbitMQConfig;
import com.ureca.snac.notification.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final SimpMessagingTemplate messaging;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void onNotification(NotificationDTO req) {
        messaging.convertAndSendToUser(
                req.getTarget(),
                "/queue/notifications",
                new WebSocketNotification(
                        req.getNotificationType().name(),
                        req.getSender(),
                        req.getTradeId()
                )
        );
    }

    @RabbitListener(queues = RabbitMQConfig.MATCHING_NOTIFICATION_QUEUE)
    public void onMatchingNotification(CardDto cardDto, @Header("amqp_receivedRoutingKey") String routingKey) {
        String username = routingKey.substring("matching.notification.".length());

        messaging.convertAndSendToUser(
                username,
                "/queue/notifications",
                cardDto
        );
    }

    public record WebSocketNotification(
            String type,
            String sender,
            Long tradeId
    ) {}
}
