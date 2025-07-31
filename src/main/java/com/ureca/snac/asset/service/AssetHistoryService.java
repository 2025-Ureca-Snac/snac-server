package com.ureca.snac.asset.service;

import com.ureca.snac.asset.dto.AssetHistoryListRequest;
import com.ureca.snac.asset.dto.AssetHistoryResponse;
import com.ureca.snac.common.CursorResult;

/**
 * AssetHistory 역할과 책임을 명세 비즈니스 로직
 * 외부 계약 내부계약 구분해야됨
 */
public interface AssetHistoryService {

    /**
     * 컨트롤러로 부터 요청을 처리하는 API 조회서비스
     * 인증된 사용자 를 받아 처리 외부세계이어주는 레이어
     *
     * @param username 조회할 사용자의 email
     * @param request  모든 필터링 및 페이징 정보 DTO
     * @return 페이지네이션 결과가 담긴 CursorResult
     */
    CursorResult<AssetHistoryResponse> getAssetHistories(String username, AssetHistoryListRequest request);
}

// LastestBalance랑 Wallet이랑 고려
// 그리고 새로운 내부