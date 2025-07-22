package com.ureca.snac.asset.dto;

import com.ureca.snac.asset.entity.AssetHistory;

import java.util.List;
import java.util.stream.Collectors;

public record AssetHistoryListResponse(
        List<AssetHistoryResponse> histories, // 실제 데이터 목록
        String nextCursor // 페이지 다음 커서
) {
    public static AssetHistoryListResponse of(List<AssetHistory> histories, int pageSize) {
        // 1. 엔티티 목록을 응답 DTO로 변환
        List<AssetHistoryResponse> historyResponses = histories.stream()
                .map(AssetHistoryResponse::from)
                .collect(Collectors.toList());

        // 커서 생성
        String nextCursor = null;
        if (histories.size() == pageSize) {
            // 페이징 계약할 때
            // 조회된거랑 요청된게 같으면 다음페이지 존재 가능성 있음
            //  커서 발급
            AssetHistory lastHistory = histories.get(histories.size() - 1);
            nextCursor = lastHistory.getCreatedAt().toString() + "," + lastHistory.getId();
        }
        return new AssetHistoryListResponse(historyResponses, nextCursor);
    }
}