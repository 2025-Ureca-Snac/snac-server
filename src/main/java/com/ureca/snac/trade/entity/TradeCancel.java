package com.ureca.snac.trade.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "trade_cancel")
public class TradeCancel extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // 거래 1:1
    @JoinColumn(name = "trade_id", nullable = false, unique = true)
    private Trade trade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Member requester;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CancelReason reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CancelStatus status;

    private LocalDateTime resolvedAt; // 상태변화 시간 저장

    // 수락
    public void accept() {
        this.status = CancelStatus.ACCEPTED;
        this.resolvedAt = LocalDateTime.now();
    }
    // 거부
    public void reject() {
        this.status = CancelStatus.REJECTED;
        this.resolvedAt = LocalDateTime.now();
    }

    @Builder
    public TradeCancel(Trade trade, Member requester, CancelReason reason, CancelStatus status, LocalDateTime resolvedAt) {
        this.trade = trade;
        this.requester = requester;
        this.reason = reason;
        this.status = status;
        this.resolvedAt = resolvedAt;
    }
}