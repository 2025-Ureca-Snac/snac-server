package com.ureca.snac.trade.repository;

import com.ureca.snac.trade.entity.Dispute;
import com.ureca.snac.trade.entity.DisputeAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisputeAttachmentRepository extends JpaRepository<DisputeAttachment, Long> {
    List<DisputeAttachment> findByDispute(Dispute dispute);
}
