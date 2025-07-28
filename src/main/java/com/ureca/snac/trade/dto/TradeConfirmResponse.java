package com.ureca.snac.trade.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "거래 확정 성공 응답 DTO for 단골 등록")
public record TradeConfirmResponse(
        @Schema(description = "거래 Id")
        Long tradeId,

        @Schema(description = "거래 상대방의 ID")
        Long counterId,

        @Schema(description = "상대방 닉네임")
        String counterNickname,

        @Schema(description = "여부")
        boolean isFavorite
) {
}
