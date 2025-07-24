package com.ureca.snac.asset.repository;

import com.ureca.snac.asset.dto.AssetHistoryListRequest;
import com.ureca.snac.asset.entity.AssetHistory;
import org.springframework.data.domain.Slice;

/**
 * QueryDSL 사용하여 복잡한 동적 쿼리를 작성하기 위한 커스텀 레포지토리 인터페이스
 */
public interface AssetHistoryRepositoryCustom {
    /**
     * 네이버 페이 화면과 동일한 필터링 조건(월별, 적립/사용, 카테고리) 지원하는
     * 조회 메소드
     *
     * @param memberId 조회할 회원 ID
     * @param request  모든 필터링 조건을 담고 있는 DTO
     * @return 필터링 및 페이징 처리된 자산 내역 목록 SLice 콘텐츠 + 다음페이지 존재 여부
     */
    Slice<AssetHistory> findWithFilters(Long memberId, AssetHistoryListRequest request);
}
