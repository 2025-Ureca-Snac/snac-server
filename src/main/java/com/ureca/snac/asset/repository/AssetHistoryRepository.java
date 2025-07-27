package com.ureca.snac.asset.repository;

import com.ureca.snac.asset.entity.AssetHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA + Query DSL
 * findById, save나 findWithFilters 다 쓸 수 있다
 */
public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long>,
        AssetHistoryRepositoryCustom {
}
