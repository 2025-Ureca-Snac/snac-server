package com.ureca.snac.swagger.annotation.response;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "201", description = "리소스 생성 성공",
        content = @Content(schema = @Schema(implementation = com.ureca.snac.common.ApiResponse.class)))
public @interface ApiCreatedResponse {
    @AliasFor(annotation = ApiResponse.class, attribute = "description")
    String description() default "리소스 생성 성공";
}
