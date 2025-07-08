package com.ureca.snac.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {

    private final String code;
    private final HttpStatus status;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    private ApiResponse(BaseCode baseCode, T data) {
        this.code = baseCode.getCode();
        this.status = baseCode.getStatus();
        this.message = baseCode.getMessage();
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 주어진 BaseCode와 데이터를 사용해 ApiResponse를 생성합니다.
     *
     * @param code 상태 코드, HTTP 상태, 메시지를 담은 BaseCode
     * @param data 응답 페이로드 (없으면 {@code null})
     * @param <T>  페이로드 타입
     * @return ApiResponse 인스턴스
     */
    public static <T> ApiResponse<T> of(BaseCode code, T data) {
        return new ApiResponse<>(code, data);
    }

    /**
     * 주어진 BaseCode를 사용해 데이터 없이 성공 응답을 생성합니다.
     *
     * @param code 상태 코드, HTTP 상태, 메시지를 담은 BaseCode
     * @param <T> 페이로드 타입
     * @return ApiResponse 인스턴스
     */
    public static <T> ApiResponse<T> ok(BaseCode code) {
        return new ApiResponse<>(code, null);
    }

    public static <T> ApiResponse<T> error(BaseCode code) {
        return new ApiResponse<>(code, null);
    }
}
