package com.ureca.snac.auth.service;

import com.ureca.snac.auth.dto.TokenDto;
import com.ureca.snac.auth.exception.RefreshTokenException;
import com.ureca.snac.auth.util.JWTUtil;
import com.ureca.snac.auth.refresh.Refresh;
import com.ureca.snac.auth.repository.RefreshRepository;
import com.ureca.snac.common.BaseCode;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReissueServiceImpl implements ReissueService {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public TokenDto reissue(String refresh) {

        // 1. 리프레시 토큰 있는지 검증
        if (refresh == null) {
            throw new RefreshTokenException(BaseCode.REFRESH_TOKEN_NULL);
        }

        // 2. 만료 췍
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            refreshRepository.findByRefresh(refresh).ifPresent(refreshRepository::delete);
            throw new RefreshTokenException(BaseCode.REFRESH_TOKEN_EXPIRED);
        }

        // 3. 리프레시 토큰 맞는지 췍
        String category = jwtUtil.getCategory(refresh);
        if (!"refresh".equals(category)) {
            throw new RefreshTokenException(BaseCode.INVALID_REFRESH_TOKEN);
        }

        // 4. 레디스에 저장된 토큰인지 확인
        String username = jwtUtil.getUsername(refresh);
        Refresh storedRefresh = refreshRepository.findByRefresh(refresh)
                .orElseThrow(() -> new RefreshTokenException(BaseCode.INVALID_REFRESH_TOKEN));

        if (!storedRefresh.getEmail().equals(username)) {
            throw new RefreshTokenException(BaseCode.INVALID_REFRESH_TOKEN);
        }
        String role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createJwt("access", username, role, 43200000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);


        // 5. 기존 리프레시 토큰 레디스에서 삭제, 새 거 저장 => 생각해보니까 굳이 삭제 할 필요가 없고 덮어씌우면 되어서 코드 변경
        refreshRepository.save(new Refresh(username, newRefresh));

        return new TokenDto(newAccess, newRefresh);
    }

}
