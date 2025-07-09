package com.ureca.snac.trade.entity;

import com.ureca.snac.common.BaseTimeEntity;
import com.ureca.snac.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "wallet_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalletHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_id", nullable = false)
    private Trade trade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private WalletHistoryType type;

    @Builder
    public WalletHistory(Trade trade, Member member, WalletHistoryType type) {
        this.trade = trade;
        this.member = member;
        this.type = type;
    }
}