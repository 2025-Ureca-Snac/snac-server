package com.ureca.snac.swagger.annotation.response;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(responseCode = "204", description = "콘텐츠가 없습니다.",
        content = @Content(schema = @Schema(hidden = true)))
public @interface ApiNoContentResponse {
    @AliasFor(annotation = ApiResponse.class, attribute = "description")
    String description() default "콘텐츠 없음";
}
