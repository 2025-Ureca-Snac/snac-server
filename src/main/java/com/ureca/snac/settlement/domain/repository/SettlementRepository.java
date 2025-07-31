package com.ureca.snac.settlement.domain.repository;

import com.ureca.snac.settlement.domain.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
}
