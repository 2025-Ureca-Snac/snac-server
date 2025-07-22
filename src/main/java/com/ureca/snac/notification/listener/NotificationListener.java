package com.ureca.snac.notification.listener;

import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.config.RabbitMQConfig;
import com.ureca.snac.trade.dto.RetrieveFilterDto;
import com.ureca.snac.trade.dto.TradeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final SimpMessagingTemplate messaging;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void onNotification(TradeDto tradeDto, @Header("amqp_receivedRoutingKey") String routingKey) {
        String username = routingKey.substring("notification.".length());

        messaging.convertAndSendToUser(
                username,
                "/queue/trade",
                tradeDto
        );
    }

    @RabbitListener(queues = RabbitMQConfig.MATCHING_NOTIFICATION_QUEUE)
    public void onMatchingNotification(CardDto cardDto, @Header("amqp_receivedRoutingKey") String routingKey) {
        String username = routingKey.substring("matching.notification.".length());

        messaging.convertAndSendToUser(
                username,
                "/queue/matching",
                cardDto
        );
    }

    @RabbitListener(queues = RabbitMQConfig.CONNECTED_USERS_QUEUE)
    public void onConnectedUsersCount(Integer count) {
        messaging.convertAndSend("/topic/connected-users", count);
    }

    @RabbitListener(queues = RabbitMQConfig.BROADCAST_QUEUE)
    public void onBroadcast(String message) {
        messaging.convertAndSend("/topic/broadcast", message);
    }

    @RabbitListener(queues = RabbitMQConfig.FILTER_QUEUE)
    public void onFilter(RetrieveFilterDto dto) {
        messaging.convertAndSendToUser(
                dto.getUsername(),
                "/queue/filters",
                dto.getBuyerFilter()
        );
    }

    public record WebSocketNotification(
            String type,
            String sender,
            Long tradeId
    ) {}
}
