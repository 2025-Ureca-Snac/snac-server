package com.ureca.snac.notification.service;

import com.ureca.snac.member.Member;
import com.ureca.snac.notification.entity.NotificationType;
import com.ureca.snac.trade.entity.Trade;

public interface NotificationService {
    void notify(Member from, Member to, NotificationType type, Trade trade);
}