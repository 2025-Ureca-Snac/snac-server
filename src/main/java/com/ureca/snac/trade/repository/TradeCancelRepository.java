package com.ureca.snac.trade.repository;

import com.ureca.snac.trade.entity.CancelStatus;
import com.ureca.snac.trade.entity.TradeCancel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TradeCancelRepository extends JpaRepository<TradeCancel, Long> {

    Optional<TradeCancel> findByTradeId(Long tradeId);

//    List<TradeCancel> findByStatusAndCreatedAtBefore(CancelStatus status, LocalDateTime before);
//
    boolean existsByTradeIdAndStatus(Long tradeId, CancelStatus status);
}