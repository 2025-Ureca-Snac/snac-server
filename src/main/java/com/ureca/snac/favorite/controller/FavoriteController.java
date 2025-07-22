package com.ureca.snac.favorite.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.favorite.dto.CursorResult;
import com.ureca.snac.favorite.dto.FavoriteCreateRequest;
import com.ureca.snac.favorite.dto.FavoriteMemberDto;
import com.ureca.snac.favorite.service.FavoriteService;
import com.ureca.snac.swagger.annotation.UserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.*;

@RestController
@RequiredArgsConstructor
public class FavoriteController implements FavoriteSwagger {

    private final FavoriteService favoriteService;

    @Override
    public ResponseEntity<ApiResponse<Void>> createFavorite(
            @Valid @RequestBody FavoriteCreateRequest request,
            @UserInfo CustomUserDetails userDetails) {
        Long currentMemberId = userDetails.getMember().getId();
        favoriteService.createFavorite(currentMemberId, request.toMemberId());

        return ResponseEntity.ok(ApiResponse.ok(FAVORITE_CREATE_SUCCESS));
    }

    @Override
    public ResponseEntity<ApiResponse<CursorResult<FavoriteMemberDto>>> getMyFavorites(
            @RequestParam Long cursorId,
            @RequestParam int size,
            @UserInfo CustomUserDetails userDetails) {
        Long currentMemberId = userDetails.getMember().getId();
        CursorResult<FavoriteMemberDto> result =
                favoriteService.getMyFavorites(currentMemberId, cursorId, size);

        return ResponseEntity.ok(ApiResponse.of(FAVORITE_LIST_SUCCESS, result));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteFavorite(
            @PathVariable Long toMemberId,
            @UserInfo CustomUserDetails userDetails) {
        Long currentMemberId = userDetails.getMember().getId();
        favoriteService.deleteFavorite(currentMemberId, toMemberId);

        return ResponseEntity.ok(ApiResponse.ok(FAVORITE_DELETE_SUCCESS));
    }
}
