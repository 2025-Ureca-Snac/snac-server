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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static com.ureca.snac.common.BaseCode.*;

@RestController
@RequiredArgsConstructor
public class FavoriteController implements FavoriteSwagger {

    private final FavoriteService favoriteService;

    @Override
    public ResponseEntity<ApiResponse<Void>> createFavorite(
            @Valid @RequestBody FavoriteCreateRequest request,
            @UserInfo CustomUserDetails userDetails) {
        String currentUserEmail = userDetails.getUsername();
        favoriteService.createFavorite(currentUserEmail, request.toMemberId());

        return ResponseEntity.status(FAVORITE_CREATE_SUCCESS.getStatus())
                .body(ApiResponse.ok(FAVORITE_CREATE_SUCCESS));
    }

    @Override
    public ResponseEntity<ApiResponse<CursorResult<FavoriteMemberDto>>> getMyFavorites(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime cursorCreatedAt,
            @RequestParam(required = false) Long cursorId,
            @RequestParam(required = false, defaultValue = "10")
            Integer size,
            @UserInfo CustomUserDetails userDetails) {

        String currentUserEmail = userDetails.getUsername();
        CursorResult<FavoriteMemberDto> result =
                favoriteService.getMyFavorites(
                        currentUserEmail, cursorCreatedAt, cursorId, size);

        return ResponseEntity.ok(ApiResponse.of(FAVORITE_LIST_SUCCESS, result));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> deleteFavorite(
            @PathVariable Long toMemberId,
            @UserInfo CustomUserDetails userDetails) {
        String currentUserEmail = userDetails.getUsername();
        favoriteService.deleteFavorite(currentUserEmail, toMemberId);

        return ResponseEntity.ok(ApiResponse.ok(FAVORITE_DELETE_SUCCESS));
    }
}
