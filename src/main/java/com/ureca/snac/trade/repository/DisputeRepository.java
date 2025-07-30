package com.ureca.snac.trade.repository;

import com.ureca.snac.trade.entity.Dispute;
import com.ureca.snac.trade.entity.DisputeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DisputeRepository extends JpaRepository<Dispute, Long> , DisputeRepositoryCustom {
    List<Dispute> findByStatus(DisputeStatus status);

    // 활성 신고 존재 여부 (IN_PROGRESS, NEED_MORE)
    boolean existsByTradeIdAndStatusIn(Long tradeId, Collection<DisputeStatus> statuses);

    // REPORTED 전환을 적용한 최초 신고 검색
    Optional<Dispute> findTopByTradeIdAndReportedAppliedTrueOrderByCreatedAtAsc(Long tradeId);
}