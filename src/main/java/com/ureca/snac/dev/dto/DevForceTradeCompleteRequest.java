package com.ureca.snac.dev.dto;

import com.ureca.snac.board.entity.constants.CardCategory;
import com.ureca.snac.board.entity.constants.Carrier;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 개발용 강제 거래 완료 요청 DTO
 * 거래 요청/ 수락 과정 모두 생략
 * 즉시 거래 데이터 생성
 */
@Schema(description = "개발용 강제 거래 완료 요청")
public record DevForceTradeCompleteRequest(

        @Schema(description = "판매글 또는 구매글 을 올린 사람의 이메일", example = "speed9911@naver.com")
        String cardOwnerEmail,

        @Schema(description = "거래 상대방의 이메일", example = "calmdown0111@gmail.com")
        String counterEmail,

        @NotNull
        @Schema(description = "생성할 카드의 종류 판매글 or 구매글", example = "SELL")
        CardCategory cardCategory,

        @NotNull
        @Schema(description = "거래할 통신사")
        Carrier carrier,

        @NotNull
        @Schema(description = "거래할 데이터 용량", example = "2")
        Integer dataAmount,

        @Schema(description = "거래에 상요할 머니", example = "2000")
        Long moneyAmountToUse,

        @Schema(description = "거래에 사용할 포인트", example = "1000")
        Long pointAmountToUse
) {
}
