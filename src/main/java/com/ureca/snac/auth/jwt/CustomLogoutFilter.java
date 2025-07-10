package com.ureca.snac.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.auth.repository.RefreshRepository;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final ObjectMapper objectMapper;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshRepository refreshRepository, ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        if (!requestUri.startsWith("/api/logout")) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }

        // 널 체크
        if (refresh == null) {
            sendErrorResponse(response, BaseCode.REFRESH_TOKEN_NULL);
            return;
        }

        // 만료 체크
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, BaseCode.REFRESH_TOKEN_EXPIRED);
            return;
        }

        // 리프레시 맞는지 체크
        if (!"refresh".equals(jwtUtil.getCategory(refresh))) {
            sendErrorResponse(response, BaseCode.INVALID_REFRESH_TOKEN);
            return;
        }

        //레디스에 토큰 존재하면 삭제
        refreshRepository.findByRefresh(refresh).ifPresent(refreshRepository::delete);


        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/api");

        response.addCookie(cookie);


        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json; charset=UTF-8");
        ApiResponse<Void> apiResponse = ApiResponse.ok(BaseCode.LOGOUT_SUCCESS);
        String responseBody = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().print(responseBody);
        response.getWriter().flush();
    }

    private void sendErrorResponse(HttpServletResponse response, BaseCode baseCode) throws IOException {
        response.setStatus(baseCode.getStatus().value());
        response.setContentType("application/json; charset=UTF-8");
        ApiResponse<Void> apiResponse = ApiResponse.error(baseCode);
        String responseBody = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().print(responseBody);
        response.getWriter().flush();
    }
}