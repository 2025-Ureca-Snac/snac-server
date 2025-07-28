package com.ureca.snac.favorite.dto;

import io.swagger.v3.oas.annotations.media.Schema;


public record FavoriteListRequest(

        @Schema(description = "다음 페이지 조회를 위한 커서 문자열, 이전 응답의 nextCursor 상요")
        String cursor,

        @Schema(description = "한 페이지에 보여줄 항복 수 ")
        Integer size
) {
}
