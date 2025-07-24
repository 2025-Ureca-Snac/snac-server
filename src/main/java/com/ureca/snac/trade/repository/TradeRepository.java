package com.ureca.snac.trade.repository;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.member.Member;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long>, CustomTradeRepository {
    Optional<Trade> findByCardIdAndBuyerIdAndStatusNot(Long cardId, Long buyerId, TradeStatus status);
    Optional<Trade> findByIdAndStatus(Long id, TradeStatus status);   // 수락용

    @EntityGraph(attributePaths = {"seller", "buyer"})
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Trade> findLockedById(Long tradeId);

    long countBySellerAndStatusIn(Member seller, Collection<TradeStatus> statuses);
    long countByBuyerAndStatusIn(Member buyer, Collection<TradeStatus> statuses);

    List<Trade> findAllByStatusAndCarrierAndCreatedAtBetween(TradeStatus status, Carrier carrier, LocalDateTime start, LocalDateTime end);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Trade> findLockedByCardId(Long cardId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select t
          from Trade t
         where t.status = :status
           and t.updatedAt < :limit
    """)
    List<Trade> findByStatusAndUpdatedAtBefore(@Param("status") TradeStatus status,
                                               @Param("limit") LocalDateTime limit);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Trade> findLockedByCardIdAndStatus(Long cardId, TradeStatus tradeStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Trade> findLockedByCardIdAndBuyer(Long cardId, Member buyer);
}
