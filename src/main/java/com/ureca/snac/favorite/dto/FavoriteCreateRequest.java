package com.ureca.snac.favorite.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "단골 등록 요청 DTO")
public record FavoriteCreateRequest(
        @NotNull(message = "단골로 등록할 회원 ID는 필수")
        @Schema(description = "단골로 등록당할 회원의 ID")
        Long toMemberId
) {
}
