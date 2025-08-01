package com.ureca.snac.notification.listener;

import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.config.RabbitMQConfig;
import com.ureca.snac.trade.dto.CancelTradeDto;
import com.ureca.snac.trade.dto.RetrieveFilterDto;
import com.ureca.snac.trade.dto.SocketErrorDto;
import com.ureca.snac.trade.dto.TradeDto;
import com.ureca.snac.trade.dto.dispute.DisputeNotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("!scheduler")
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

    // 취소 관련 리스너
    @RabbitListener(queues = RabbitMQConfig.CANCEL_QUEUE)
    public void onTradeCancel(CancelTradeDto cancelDto) {
        log.info("[거래 취소] 사용자: {}, tradeId: {}", cancelDto.getUsername(), cancelDto.getTradeDto().getTradeId());

        // WebSocket으로 /user/queue/cancel로 전송
        messaging.convertAndSendToUser(
                cancelDto.getUsername(),       // 대상 사용자
                "/queue/cancel",               // 목적지
                cancelDto                      // 보낼 DTO(메시지)
        );
    }


    // 에러 관련 이벤트 리스너
    @RabbitListener(queues = RabbitMQConfig.ERROR_QUEUE)
    public void onSocketError(SocketErrorDto errorDto) {
        log.warn("[에러 알림] 사용자: {}, error: {}, message: {}", errorDto.getUsername(), errorDto.getBaseCode().name(), errorDto.getBaseCode().getMessage());
        messaging.convertAndSendToUser(
                errorDto.getUsername(),
                "/queue/errors",
                errorDto.getBaseCode().name()
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

    // 신고 알림 리스너 추가
    @RabbitListener(queues = RabbitMQConfig.DISPUTE_NOTIFICATION_QUEUE)
    public void onDisputeNotification(DisputeNotificationDto disputeDto, @Header("amqp_receivedRoutingKey") String routingKey) {
        String username = routingKey.substring("dispute.notification.".length());

        log.info("[신고 알림] 사용자: {}, disputeId: {}", username, disputeDto.getDisputeId());
        messaging.convertAndSendToUser(
                username,
                "/queue/dispute",
                disputeDto
        );
    }

    public record WebSocketNotification(
            String type,
            String sender,
            Long tradeId
    ) {}
}
