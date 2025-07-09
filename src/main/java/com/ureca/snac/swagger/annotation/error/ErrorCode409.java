package com.ureca.snac.swagger.annotation.error;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.annotation.AliasFor;
import org.springframework.http.ProblemDetail;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Documented
@Target(METHOD)
@Retention(RUNTIME)
@ApiResponse(responseCode = "409", description = "데이터 충돌",
        content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
public @interface ErrorCode409 {
    @AliasFor(annotation = ApiResponse.class, attribute = "description")
    String description() default "데이터 충돌";
}
