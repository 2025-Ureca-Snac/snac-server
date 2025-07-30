package com.ureca.snac.trade.repository;

import com.ureca.snac.member.Member;
import com.ureca.snac.trade.entity.Dispute;
import com.ureca.snac.trade.entity.DisputeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DisputeRepository extends JpaRepository<Dispute, Long> , DisputeRepositoryCustom {
    List<Dispute> findByStatus(DisputeStatus status);

    // 활성 신고 존재 여부 (IN_PROGRESS, NEED_MORE)
    boolean existsByTradeIdAndStatusIn(Long tradeId, Collection<DisputeStatus> statuses);

    // REPORTED 전환을 적용한 최초 신고 검색
    Optional<Dispute> findTopByTradeIdAndReportedAppliedTrueOrderByCreatedAtAsc(Long tradeId);

    // 내가 신고한 목록
    Page<Dispute> findByReporterOrderByCreatedAtDesc(Member reporter, Pageable pageable);

    // 내가 ‘신고받은’ 목록 (해당 거래의 당사자이긴 하지만 reporter != me)
    @Query("""
      select d from Dispute d
       where (d.trade.seller = :me or d.trade.buyer = :me)
         and d.reporter <> :me
       order by d.createdAt desc
    """)
    Page<Dispute> findReceivedByParticipant(@Param("me") Member me, Pageable pageable);

}