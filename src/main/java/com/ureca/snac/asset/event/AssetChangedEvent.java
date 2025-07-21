package com.ureca.snac.asset.event;

import com.ureca.snac.asset.entity.AssetType;
import com.ureca.snac.asset.entity.SourceDomain;
import com.ureca.snac.asset.entity.TransactionCategory;
import com.ureca.snac.asset.entity.TransactionType;
import lombok.Builder;
import lombok.NonNull;

/**
 * 자산 변동이 발생했을 때 발행되는 이벤트 레코드
 * 데이터 전달 계약서 역할 contract
 * 작업 지시서
 */
@Builder
public record AssetChangedEvent(
        @NonNull Long memberId,
        @NonNull AssetType assetType,
        @NonNull TransactionType transactionType,
        @NonNull TransactionCategory category,
        @NonNull Long amount,
        @NonNull Long balanceAfter,
        @NonNull String title,
        @NonNull SourceDomain sourceDomain,
        Long sourceId
) {
}
