package com.ureca.snac.asset.repository;

import com.ureca.snac.asset.entity.AssetHistory;
import com.ureca.snac.asset.entity.AssetType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA + Query DSL
 * findById, save나 findWithFilters 다 쓸 수 있다
 */
public interface AssetHistoryRepository extends JpaRepository<AssetHistory, Long>,
        AssetHistoryRepositoryCustom {
    /**
     * 특정 회원 특정 자산 타입에 대한 가장 최근의 잔액
     * 고정된 쿼리라서 QueryDSL로 전환하지 않고 그대로
     * JPQL 사용
     * <p>
     * 리팩토링
     * JPA 를 써서 하는게 더 낫다
     * JPQL에 대한 안정성이 낮다 JPA 보다
     * 필드 이름변경시 함께 수정해야됨
     *
     * @param memberId  회원 Id
     * @param assetType 자산 타입
     * @return 가장 최근의 balanceAfter 값
     */
    Optional<Long> findFirstByMemberIdAndAssetTypeOrderByCreatedAtDescIdDesc(
            Long memberId, AssetType assetType);
}
