package com.ureca.snac.favorite.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "단골 여부 확인 응답 DTO")
public record FavoriteCheckResponse(
        @Schema(description = "단골 여부")
        boolean isFavorite
) {
}
