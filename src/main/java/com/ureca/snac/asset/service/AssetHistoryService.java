package com.ureca.snac.asset.service;

import com.ureca.snac.asset.dto.AssetHistoryListRequest;
import com.ureca.snac.asset.dto.AssetHistoryListResponse;
import com.ureca.snac.asset.entity.AssetType;
import com.ureca.snac.asset.event.AssetChangedEvent;

/**
 * AssetHistory 역할과 책임을 명세 비즈니스 로직
 * 외부 계약 내부계약 구분해야됨
 */
public interface AssetHistoryService {
    /**
     * 특정 회원의 특정 자산에 대한 최신 잔액 조회
     * 내부계약으로 다른 서비스가 새로운 변동 후 계산을 위함
     *
     * @param memberId  특정 회원
     * @param assetType 특정 자산 머니, 포인트
     * @return 최신 잔액 없으면 OL 반환
     */
    Long getLatestBalance(Long memberId, AssetType assetType);

    /**
     * 이벤트를 받아서 새로운 내역 생성 저장
     * 내부 계약
     *
     * @param event 자산 변동 정보 이벤트
     */
    void handleAssetChangedEvent(AssetChangedEvent event);


    /**
     * 컨트롤러로 부터 요청을 처리하는 API 조회서비스
     * 인증된 사용자 를 받아 처리 외부세계이어주는 레이어
     *
     * @param username 조회할 사용자의 email
     * @param request  모든 필터링 및 페이징 정보 DTO
     * @return 필터링된 엔티티 목록
     */
    AssetHistoryListResponse getAssetHistories(String username, AssetHistoryListRequest request);
}

// LastestBalance랑 Wallet이랑 고려
// 그리고 새로운 내부