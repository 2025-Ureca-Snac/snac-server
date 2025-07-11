package com.ureca.snac.notification.dto;

import com.ureca.snac.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotificationMessage {
    private Long memberToId;
    private NotificationType type;
    private Long tradeId;
}
