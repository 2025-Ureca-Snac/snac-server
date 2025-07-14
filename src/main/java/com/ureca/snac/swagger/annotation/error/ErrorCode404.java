package com.ureca.snac.swagger.annotation.error;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.ProblemDetail;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "404", description = "리스소 찾을 수 없습니다",
        content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
public @interface ErrorCode404 {
    @AliasFor(annotation = ApiResponse.class, attribute = "description")
    String description() default "리스소 찾을 수 없습니다";
}
