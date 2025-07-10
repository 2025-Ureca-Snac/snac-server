package com.ureca.snac.trade.repository;

import com.ureca.snac.trade.entity.TradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeHistoryRepository extends JpaRepository<TradeHistory, Long> {}