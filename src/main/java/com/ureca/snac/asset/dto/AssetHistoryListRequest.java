package com.ureca.snac.asset.dto;

import com.ureca.snac.asset.entity.AssetType;
import com.ureca.snac.asset.entity.TransactionCategory;
import com.ureca.snac.asset.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 내부 객체 VO 느낌 HTTP 요청의 세부사항을 알필요 없이
 * 비즈니스로직에서 사용되는 객체
 */
public record AssetHistoryListRequest(
        // 어떤 종류 자산 내역
        @Schema(description = "자산 종류 머니 or 포인트",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        AssetType assetType,

        // 입금 vs 출금 같은 필터링 null 이면 모든거
        @Schema(description = "거래 종류 입금 or 출금")
        TransactionType transactionType,

        // 월별 필터링, null 이면 전체 기간
        @Schema(description = "조회할 년월 (yyyy-MM)")
        String yearMonth,

        // 결제 충전 등 세부 카테고리 필터링
        @Schema(description = "세부 거래 카테고리")
        TransactionCategory category,

        @Schema(description = "다음 페이지 조회를 위한 커서 문자열")
        String cursor,

        @Schema(description = "한 페이지의 보여줄 수", defaultValue = "20")
        @NotNull
        Integer size
) {
}
