package com.ureca.snac.notification.service;

import com.ureca.snac.notification.dto.NotificationDTO;

public interface NotificationService {
    void notify(NotificationDTO notificationRequest);
}
