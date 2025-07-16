package com.ureca.snac.trade.repository;

import com.ureca.snac.member.Member;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Collection;
import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long>, CustomTradeRepository {
    Optional<Trade> findByCardIdAndBuyerIdAndStatusNot(Long cardId, Long buyerId, TradeStatus status);
    Optional<Trade> findByIdAndStatus(Long id, TradeStatus status);   // 수락용

    @EntityGraph(attributePaths = {"seller", "buyer"})
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Trade> findLockedById(Long tradeId);

    long countBySellerAndStatusIn(Member seller, Collection<TradeStatus> statuses);
    long countByBuyerAndStatusIn(Member buyer, Collection<TradeStatus> statuses);
}
