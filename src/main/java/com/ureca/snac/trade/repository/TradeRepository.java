package com.ureca.snac.trade.repository;

import com.ureca.snac.board.entity.constants.Carrier;
import com.ureca.snac.member.Member;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
import com.ureca.snac.trade.entity.TradeType;
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

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

public interface TradeRepository extends JpaRepository<Trade, Long>, CustomTradeRepository {
    Optional<Trade> findByCardIdAndBuyerIdAndStatusNot(Long cardId, Long buyerId, TradeStatus status);
    Optional<Trade> findByIdAndStatus(Long id, TradeStatus status);   // 수락용

    @Query("SELECT t FROM Trade t WHERE t.id = :id AND (t.buyer = :member OR t.seller = :member)")
    Optional<Trade> findByIdAndParticipant(@Param("id") Long id, @Param("member") Member member);

    @EntityGraph(attributePaths = {"seller", "buyer"})
    @Lock(PESSIMISTIC_WRITE)
    Optional<Trade> findLockedById(Long tradeId);

    long countBySellerAndStatusIn(Member seller, Collection<TradeStatus> statuses);
    long countByBuyerAndStatusIn(Member buyer, Collection<TradeStatus> statuses);

    List<Trade> findAllByStatusAndCarrierAndCreatedAtBetween(TradeStatus status, Carrier carrier, LocalDateTime start, LocalDateTime end);

    @Lock(PESSIMISTIC_WRITE)
    Optional<Trade> findLockedByCardId(Long cardId);

    @Lock(PESSIMISTIC_WRITE)
    @Query("""
        select t
          from Trade t
         where t.status = :status
           and t.updatedAt < :limit
    """)
    List<Trade> findByStatusAndUpdatedAtBefore(@Param("status") TradeStatus status,
                                               @Param("limit") LocalDateTime limit);


    @Lock(PESSIMISTIC_WRITE)
    @Query("""
  select t from Trade t
   where t.status = :status
     and t.updatedAt < :limit
     and t.autoConfirmPaused = false
""")
    List<Trade> findByStatusAndUpdatedAtBeforeAndAutoConfirmPausedFalse(
            @Param("status") TradeStatus status,
            @Param("limit")  LocalDateTime limit);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Trade> findLockedByCardIdAndStatus(Long cardId, TradeStatus tradeStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Trade> findLockedByCardIdAndBuyer(Long cardId, Member buyer);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Trade> findAllByBuyerAndTradeType(Member member, TradeType tradeType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Trade> findAllBySellerAndTradeType(Member member, TradeType tradeType);

    @Lock(PESSIMISTIC_WRITE)
    Optional<Trade> findByBuyerAndStatus(Member member, TradeStatus status);

}
