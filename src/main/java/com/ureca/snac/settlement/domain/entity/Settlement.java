package com.ureca.snac.settlement.domain.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.settlement.exception.InvalidSettlementRequestException;
import jakarta.persistence.*;
import lombok.*;

/**
 * 정산 내역 기록 엔티티
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Settlement extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SettlementStatus status;

    private Settlement(Member member, Long amount) {
        this.member = member;
        this.amount = amount;
        this.status = SettlementStatus.SUCCESS;
    }

    public static Settlement create(Member member, Long amount) {
        if (member == null | amount == null || amount <= 0) {
            throw new InvalidSettlementRequestException();
        }
        return new Settlement(member, amount);
    }
}
