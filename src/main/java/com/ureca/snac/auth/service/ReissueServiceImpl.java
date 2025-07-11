package com.ureca.snac.auth.service;

import com.ureca.snac.auth.jwt.JWTUtil;
import com.ureca.snac.auth.refresh.Refresh;
import com.ureca.snac.auth.repository.RefreshRepository;
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
    private final RefreshRepository refreshRepository;

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

            refreshRepository.findByRefresh(refresh).ifPresent(refreshRepository::delete);

            throw new BusinessException(BaseCode.REFRESH_TOKEN_EXPIRED);
        }

        // 3. 리프레시 토큰 맞는지 췍
        String category = jwtUtil.getCategory(refresh);
        if (!"refresh".equals(category)) {
            throw new BusinessException(BaseCode.INVALID_REFRESH_TOKEN);
        }

        // 4. 레디스에 저장된 토큰인지 확인
        String username = jwtUtil.getUsername(refresh);
        Refresh storedRefresh = refreshRepository.findByRefresh(refresh)
                .orElseThrow(() -> new BusinessException(BaseCode.INVALID_REFRESH_TOKEN));

        if (!storedRefresh.getEmail().equals(username)) {
            throw new BusinessException(BaseCode.INVALID_REFRESH_TOKEN);
        }
        String role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);


        // 5. 기존 리프레시 토큰 레디스에서 삭제, 새 거 저장 => 생각해보니까 굳이 삭제 할 필요가 없고 덮어씌우면 되어서 코드 변경

        refreshRepository.save(new Refresh(username, newRefresh));

        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + newAccess);
        response.addCookie(createCookie("refresh", newRefresh));
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/api");
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");
        return cookie;
    }
}
