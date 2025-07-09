package com.ureca.snac.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.Role;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper= new ObjectMapper();
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = request.getHeader("access");

        // 토큰이 없다면 다음 필터로 넘김
        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

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

        PrintWriter writer = response.getWriter();
        writer.print(responseBody);
        writer.flush();
    }
}
