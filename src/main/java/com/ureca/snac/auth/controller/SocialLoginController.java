package com.ureca.snac.auth.controller;

import com.ureca.snac.auth.dto.TokenDto;
import com.ureca.snac.auth.exception.SocialTokenException;
import com.ureca.snac.auth.service.SocialLoginService;
import com.ureca.snac.auth.util.CookieUtil;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SocialLoginController implements SocialLoginControllerSwagger {

    private final SocialLoginService socialLoginService;

    @Override
    @PostMapping("/social-login")
    public ResponseEntity<ApiResponse<Void>> socialLogin(HttpServletRequest request, HttpServletResponse response) {
        String socialToken = extractSocialToken(request);

        TokenDto tokenDto = socialLoginService.socialLogin(socialToken);

        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDto.getAccessToken());
        response.addCookie(CookieUtil.createCookie("refresh", tokenDto.getRefreshToken()));

        return ResponseEntity.ok(ApiResponse.ok(BaseCode.OAUTH_LOGIN_SUCCESS));
    }

    private String extractSocialToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new SocialTokenException(BaseCode.SOCIAL_TOKEN_INVALID);
    }
}
