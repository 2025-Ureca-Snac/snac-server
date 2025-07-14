package com.ureca.snac.notification.service;

import com.ureca.snac.notification.config.RabbitMQConfig;
import com.ureca.snac.notification.dto.NotificationDTO;
import com.ureca.snac.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    private final RabbitTemplate rabbitTemplate;
    private static final String EXCHANGE = RabbitMQConfig.NOTIFICATION_EXCHANGE;
    private static final String RK_FMT = "%s.%s";

    @Transactional
    public void notify(NotificationDTO notificationRequest) {
        String rk = String.format("notification.%s.%s",
                notificationRequest.getNotificationType().name().toLowerCase(),
                notificationRequest.getTarget());

        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, rk, notificationRequest);
    }
}
