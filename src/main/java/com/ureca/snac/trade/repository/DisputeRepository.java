package com.ureca.snac.trade.repository;

import com.ureca.snac.trade.entity.Dispute;
import com.ureca.snac.trade.entity.DisputeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DisputeRepository extends JpaRepository<Dispute, Long> {
    List<Dispute> findByStatus(DisputeStatus status);
}