package com.ureca.snac.trade.repository;

import com.ureca.snac.trade.entity.WalletHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletHistoryRepository extends JpaRepository<WalletHistory, Long> {}