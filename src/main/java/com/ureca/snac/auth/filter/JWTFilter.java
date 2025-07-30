package com.ureca.snac.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.auth.util.JWTUtil;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.Role;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final JWTUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return path.equals("/api/social-login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("JWTFilter 호출 URI: {} ", request.getRequestURI());
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 토큰이 없다면 다음 필터로 넘김
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String accessToken = header.substring(7);

        // 토큰 만료 여부 확인
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            sendErrorResponse(response, BaseCode.TOKEN_EXPIRED);
            return;
        }

        // 토큰이 access 맞는지 확인
        String category = jwtUtil.getCategory(accessToken);
        if (!category.equals("access")) {
            sendErrorResponse(response, BaseCode.TOKEN_INVALID);
            return;
        }

        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        Member member = Member.builder()
                .email(username)
                .role(Role.valueOf(role))
                .build();
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }


    private void sendErrorResponse(HttpServletResponse response, BaseCode code) throws IOException {
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(code.getStatus().value());
        ApiResponse<Void> apiResponse = ApiResponse.error(code);
        String responseBody = objectMapper.writeValueAsString(apiResponse);
        response.getWriter().print(responseBody);
        response.getWriter().flush();
    }
}