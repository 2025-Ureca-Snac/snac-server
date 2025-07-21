package com.ureca.snac.notification.listener;

import com.ureca.snac.config.RabbitMQConfig;
import com.ureca.snac.notification.dto.NotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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

    public record WebSocketNotification(
            String type,
            String sender,
            Long tradeId
    ) {}
}
