package com.ureca.snac.trade.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "dispute")
public class Dispute extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // 거래 FK
    @JoinColumn(name = "trade_id", nullable = false)
    private Trade trade;

    @ManyToOne(fetch = FetchType.LAZY) // 신고자
    @JoinColumn(name = "reporter_id", nullable = false)
    private Member reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DisputeType type;

    @Column(columnDefinition = "TEXT")
    private String reason; // 사용자 입력

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DisputeStatus status;

    private LocalDateTime resolvedAt;

    @Builder
    public Dispute(Trade trade, Member reporter, DisputeType type, String reason) {
        this.trade = trade;
        this.reporter = reporter;
        this.type = type;
        this.reason = reason;
        this.status = DisputeStatus.OPEN;
    }

    // 상태 변경
    public void awaitingUser() {
        this.status = DisputeStatus.AWAITING_USER;
    }
    public void inReview() {
        this.status = DisputeStatus.IN_REVIEW;
    }
    public void resolve() {
        this.status = DisputeStatus.RESOLVED; this.resolvedAt = LocalDateTime.now();
    }
    public void reject() {
        this.status = DisputeStatus.REJECTED; this.resolvedAt = LocalDateTime.now();
    }

}