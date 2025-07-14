package com.ureca.snac.auth.controller;

import com.ureca.snac.auth.dto.TokenDto;
import com.ureca.snac.auth.service.ReissueService;
import com.ureca.snac.auth.util.CookieUtil;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController implements ReissueControllerSwagger {

    private final ReissueService reissueService;

    @Override
    public ResponseEntity<ApiResponse<Void>> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = getRefreshFromCookie(request);

        TokenDto tokenDto = reissueService.reissue(refresh);

        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokenDto.getAccessToken());
        response.addCookie(CookieUtil.createCookie("refresh", tokenDto.getRefreshToken()));

        return ResponseEntity.ok(ApiResponse.ok(BaseCode.REISSUE_SUCCESS));
    }

    private static String getRefreshFromCookie(HttpServletRequest request) {
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    refresh = cookie.getValue();
                }
            }
        }
        return refresh;
    }
}
