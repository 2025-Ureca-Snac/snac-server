package com.ureca.snac.notification.entity;

import com.ureca.snac.member.Member;
import com.ureca.snac.trade.entity.Trade;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @Column(name = "notification_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_from_id")
    private Member memberFrom;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_to_id")
    private Member memberTo;

    @ManyToOne
    @JoinColumn(name = "trade_id")
    private Trade trade;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

//    @Column(name = "is_read", nullable = false)
//    private boolean isRead;

    @Builder
    public Notification(Member memberFrom, Member memberTo, NotificationType type) {
        this.memberFrom = memberFrom;
        this.memberTo = memberTo;
        this.type = type;
    }
}
