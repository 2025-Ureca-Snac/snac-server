package com.ureca.snac.auth.service;

import com.ureca.snac.auth.jwt.JWTUtil;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueServiceImpl implements ReissueService {

    private final JWTUtil jwtUtil;

    @Override
    public void reissue(HttpServletRequest request, HttpServletResponse response) {
        // 1. 리프레시 토큰 쿠키로부터 가져오고
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    refresh = cookie.getValue();
                }
            }
        }

        if (refresh == null) {
            throw new BusinessException(BaseCode.REFRESH_TOKEN_NULL);
        }

        // 2. 만료 췍
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            throw new BusinessException(BaseCode.REFRESH_TOKEN_EXPIRED);
        }

        // 3. 리프레시 토큰 맞는지 췍
        String category = jwtUtil.getCategory(refresh);
        if (!"refresh".equals(category)) {
            throw new BusinessException(BaseCode.INVALID_REFRESH_TOKEN);
        }

        // 4. 새로운 토큰 발급
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);


        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newAccess);
        response.addCookie(createCookie("refresh", newRefresh));
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/api");
        cookie.setHttpOnly(true);
        return cookie;
    }
}
