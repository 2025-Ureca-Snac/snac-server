package com.ureca.snac.trade.entity;

import com.ureca.snac.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "trade_duration_statistic")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeDurationStatistic extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long durationSeconds;

    @Builder
    private TradeDurationStatistic(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }
}
