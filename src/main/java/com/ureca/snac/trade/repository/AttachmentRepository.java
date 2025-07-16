package com.ureca.snac.trade.repository;

import com.ureca.snac.trade.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    boolean existsByTradeId(Long tradeId);  // 특정 tradeId를 가진 첨부 파일이 존재하는지 확인 -> 있으면 검증 혹은 거래 상태변경? 하면 될 듯
    Optional<Attachment> findByTradeId(Long tradeId);
}