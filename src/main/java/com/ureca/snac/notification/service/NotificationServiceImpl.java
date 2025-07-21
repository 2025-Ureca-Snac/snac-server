package com.ureca.snac.notification.service;

import com.ureca.snac.board.dto.CardDto;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.config.RabbitMQConfig;
import com.ureca.snac.notification.dto.NotificationDTO;
import com.ureca.snac.notification.entity.Notification;
import com.ureca.snac.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE = RabbitMQConfig.NOTIFICATION_EXCHANGE;
    private static final String RK_FMT = "%s.%s";

    private static final String MATCHING_EXCHANGE = RabbitMQConfig.MATCHING_NOTIFICATION_EXCHANGE;

    @Transactional
    public void notify(NotificationDTO notificationRequest) {
        Member from = getMember(notificationRequest.getSender());
        Member to = getMember(notificationRequest.getTarget());

        Notification notification = Notification.builder()
                .memberFrom(from)
                .memberTo(to)
                .type(notificationRequest.getNotificationType())
                .build();

        notificationRepository.save(notification);

        String rk = String.format("notification.%s.%s",
                notificationRequest.getNotificationType().name().toLowerCase(),
                notificationRequest.getTarget());

        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, rk, notificationRequest);
    }

    public void sendMatchingNotification(String username, CardDto cardDto) {
        System.out.println("매칭알림 MQ 발행: " + username + " " + cardDto);
        String routingKey = String.format("matching.notification.%s", username);
        rabbitTemplate.convertAndSend(MATCHING_EXCHANGE, routingKey, cardDto);
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }
}
