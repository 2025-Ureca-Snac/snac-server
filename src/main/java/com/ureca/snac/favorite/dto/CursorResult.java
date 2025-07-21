package com.ureca.snac.favorite.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "커서 기반 페이지네이션 응답 DTO")
public record CursorResult<T>(
        @Schema(description = "콘텐츠 리스트")
        List<T> contents,

        @Schema(description = "다음 페이지 조회를 위한 커서 ID (다음페이지 없으면 null")
        Long nextCursorId,

        @Schema(description = "다음 페이지 존재 여부")
        boolean hasNext
) {
    public static <T> CursorResult<T> of(List<T> contents, Long nextCursorId, boolean hasNext) {
        return new CursorResult<>(contents, nextCursorId, hasNext);
    }
}
