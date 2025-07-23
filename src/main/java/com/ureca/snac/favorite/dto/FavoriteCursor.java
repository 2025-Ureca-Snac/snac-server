package com.ureca.snac.favorite.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 최신 순 정렬을 위한 복합 커서 ID
 *
 * @param createdAt 이전 페이지 마지막 생성시간
 * @param id        이전 페이지 마지막 항목 ID
 */
public record FavoriteCursor(
        @Schema(description = "커서 기준 생성 시간")
        LocalDateTime createdAt,

        @Schema(description = "커서 기준 ID")
        Long id
) {
}
