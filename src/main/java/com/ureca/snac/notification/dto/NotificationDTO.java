package com.ureca.snac.notification.dto;

import com.ureca.snac.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter @Setter
@AllArgsConstructor
public class NotificationDTO implements Serializable {
    private NotificationType notificationType;
    private String sender;
    private String target;
    private Long tradeId;
}
