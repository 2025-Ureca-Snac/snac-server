package com.ureca.snac.trade.repository;

import com.ureca.snac.trade.entity.Dispute;
import com.ureca.snac.trade.entity.DisputeComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisputeCommentRepository extends JpaRepository<DisputeComment, Long> {
    List<DisputeComment> findByDisputeOrderByCreatedAtAsc(Dispute dispute);
}