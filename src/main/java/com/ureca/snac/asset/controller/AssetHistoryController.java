package com.ureca.snac.asset.controller;

import com.ureca.snac.asset.dto.AssetHistoryListRequest;
import com.ureca.snac.asset.dto.AssetHistoryResponse;
import com.ureca.snac.asset.service.AssetHistoryService;
import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.CursorResult;
import com.ureca.snac.swagger.annotation.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.ASSET_HISTORY_SUCCESS;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AssetHistoryController implements AssetHistorySwagger {

    private final AssetHistoryService assetHistoryService;

    @Override
    public ResponseEntity<ApiResponse<CursorResult<AssetHistoryResponse>>> getMyAssetHistories(
            AssetHistoryListRequest request, @UserInfo CustomUserDetails userDetails) {
        log.info("[자산 내역 조회 시작] 회원 : {}, 요청 : {}", userDetails.getUsername(), request);

        CursorResult<AssetHistoryResponse> response =
                assetHistoryService.getAssetHistories(userDetails.getUsername(), request);

        log.info("[자산 내역 조회 완료] 회원 : {}, 조회된 내역 개수 : {}",
                userDetails.getUsername(), response.contents().size());
        return ResponseEntity.ok(ApiResponse.of(ASSET_HISTORY_SUCCESS, response));
    }
}
