package com.ureca.snac.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.auth.repository.AuthRepository;
import com.ureca.snac.auth.util.JWTUtil;
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
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;
    private final JWTUtil jwtUtil;
    private final AuthRepository authRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

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

        String category = jwtUtil.getCategory(accessToken);
        Member member;
        switch (category) {
            case "access":
                // 일반 로그인 토큰 처리
                String username = jwtUtil.getUsername(accessToken);
                String role     = jwtUtil.getRole(accessToken);
                member = Member.builder()
                        .email(username)
                        .role(Role.valueOf(role))
                        .build();
                break;

            case "social":
                // 소셜 로그인 토큰 처리
                String usernameBySocial = jwtUtil.getUsername(accessToken);
                String provider   = jwtUtil.getProvider(accessToken);
                String providerId = jwtUtil.getProviderId(accessToken);

                Member socialMember;
                switch (provider) {
                    case "naver":
                        socialMember = authRepository.findByNaverId(providerId);
                        break;
                    case "google":
                        socialMember = authRepository.findByGoogleId(providerId);
                        break;
                    case "kakao":
                        socialMember = authRepository.findByKakaoId(providerId);
                        break;
                    default:
                        sendErrorResponse(response, BaseCode.TOKEN_INVALID);
                        return;
                }

                if (!socialMember.getEmail().equals(usernameBySocial)) {
                    sendErrorResponse(response, BaseCode.TOKEN_INVALID);
                    return;
                }

                member = socialMember;
                break;

            default:
                sendErrorResponse(response, BaseCode.TOKEN_INVALID);
                return;
        }
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
