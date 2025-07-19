package com.ureca.snac.trade.repository;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.trade.entity.TradeStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TradeStatisticsRepository extends JpaRepository<TradeStatistics, Long> {
    Optional<TradeStatistics> findFirstByCarrierOrderByIdDesc(Carrier carrier);
}
