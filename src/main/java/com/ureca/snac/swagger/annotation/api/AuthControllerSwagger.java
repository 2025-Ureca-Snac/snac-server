package com.ureca.snac.swagger.annotation.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "사용자 API", description = "회원 가입, 내 정보")
@RequestMapping("/api/user")
public interface AuthControllerSwagger {
}
