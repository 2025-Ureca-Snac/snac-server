package com.ureca.snac.trade.repository;

import com.ureca.snac.trade.entity.CancelStatus;
import com.ureca.snac.trade.entity.TradeCancel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TradeCancelRepository extends JpaRepository<TradeCancel, Long> {

    Optional<TradeCancel> findByTradeId(Long tradeId);

//    List<TradeCancel> findByStatusAndCreatedAtBefore(CancelStatus status, LocalDateTime before);
//
    boolean existsByTradeIdAndStatus(Long tradeId, CancelStatus status);
    boolean existsByTradeId(Long tradeId);

    // 무한 스크롤에 표시용 요약 조회 , 대기중인 취쇼요청만 조회
    @Query("""
        select tc.trade.id as tradeId,
               tc.reason   as reason,
               tc.createdAt as createdAt
          from TradeCancel tc
         where tc.trade.id in :tradeIds
           and tc.status = com.ureca.snac.trade.entity.CancelStatus.REQUESTED
    """)
    List<TradeCancelSummary> findRequestedSummaryByTradeIds(@Param("tradeIds") Collection<Long> tradeIds);

    // Cancel Status 전부 조회
    @Query("""
    select tc.trade.id as tradeId,
           tc.reason   as reason,
           tc.status   as status,
           tc.createdAt as createdAt
      from TradeCancel tc
     where tc.trade.id in :tradeIds
    """)
    List<TradeCancelSummary> findCancelSummaryByTradeIds(@Param("tradeIds") Collection<Long> tradeIds);

    @Query("""
    select tc.trade.id as tradeId,
           tc.reason as reason,
           tc.status as status,
           tc.createdAt as createdAt
      from TradeCancel tc
     where tc.trade.id = :tradeId
    """)
    Optional<TradeCancelSummary> findSummaryByTradeId(@Param("tradeId") Long tradeId);

    interface TradeCancelSummary {
        Long getTradeId();
        com.ureca.snac.trade.entity.CancelReason getReason();
        CancelStatus getStatus();
        LocalDateTime getCreatedAt();
    }
}