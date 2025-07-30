package com.ureca.snac.trade.repository;

import com.ureca.snac.trade.entity.TradeDurationStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TradeDurationStatisticRepository extends JpaRepository<TradeDurationStatistic, Long> {

    Optional<TradeDurationStatistic> findTopByOrderByCreatedAtDesc();
}
