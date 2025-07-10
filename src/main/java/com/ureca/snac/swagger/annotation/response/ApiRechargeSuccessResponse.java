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
@ApiResponse(responseCode = "200", description = "머니 충전 요청 성공",
        content = @Content(schema = @Schema(implementation = com.ureca.snac.common.ApiResponse.class)))
public @interface ApiRechargeSuccessResponse {
    @AliasFor(annotation = ApiResponse.class, attribute = "description")
    String description() default "인증 실패";
}
