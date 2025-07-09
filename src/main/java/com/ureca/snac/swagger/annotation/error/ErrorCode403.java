package com.ureca.snac.swagger.annotation.error;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.ProblemDetail;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "403", description = "접근 권한 없음",
        content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
public @interface ErrorCode403 {
    @AliasFor(annotation = ApiResponse.class, attribute = "description")
    String description() default "접근 권한 없음";
}
