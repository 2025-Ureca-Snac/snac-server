package com.ureca.snac.trade.entity;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "trade_statistics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeStatistics extends BaseTimeEntity {

    @Id
    @Column(name = "trade_statistics_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Carrier carrier;

    private Double avgTotalPrice;

    @Builder
    private TradeStatistics(Carrier carrier, Double avgTotalPrice) {
        this.carrier = carrier;
        this.avgTotalPrice = avgTotalPrice;
    }
}
