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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private String description; // 사용자 입력

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DisputeStatus status;

    // 관리자 답변
    @Column(columnDefinition = "TEXT")
    private String answer;
    private LocalDateTime answerAt;


    @Builder
    public Dispute(Trade trade, Member reporter, DisputeType type, String description) {
        this.trade = trade;
        this.reporter = reporter;
        this.type = type;
        this.description = description;
        this.status = DisputeStatus.IN_PROGRESS;
    }

    // 상태 변경
    public void needMore(String answer) {
        this.answer     = answer;
        this.answerAt   = LocalDateTime.now();
        this.status     = DisputeStatus.NEED_MORE;
    }
    public void answered(String answer) {
        this.answer     = answer;
        this.answerAt   = LocalDateTime.now();
        this.status     = DisputeStatus.ANSWERED;
    }

    public void reject(String answer) {
        this.answer   = answer;
        this.answerAt = LocalDateTime.now();
        this.status   = DisputeStatus.REJECTED;
    }

}