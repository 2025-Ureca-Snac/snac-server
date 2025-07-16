package com.ureca.snac.infra.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 에러 응답 본문 DTO
 *
 * @param code
 * @param message
 * @param data
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TossErrorResponse(
        String code,
        String message,
        Object data
) {
}