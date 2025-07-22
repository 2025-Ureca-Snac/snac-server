package com.ureca.snac.trade.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "penalty")
public class Penalty extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PenaltyReason reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PenaltyLevel level;

    @Builder
    public Penalty(Member member, PenaltyReason reason, PenaltyLevel level) {
        this.member = member;
        this.reason = reason;
        this.level = level;
    }
}