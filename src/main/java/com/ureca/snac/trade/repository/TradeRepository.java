package com.ureca.snac.trade.repository;

import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    Optional<Trade> findByCardIdAndBuyerIdAndStatusNot(Long cardId, Long buyerId, TradeStatus status);
    Optional<Trade> findByIdAndStatus(Long id, TradeStatus status);   // 수락용

}
