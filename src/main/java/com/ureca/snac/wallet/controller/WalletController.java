package com.ureca.snac.wallet.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.wallet.dto.WalletSummaryResponse;
import com.ureca.snac.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.WALLET_SUMMARY_SUCCESS;

@RestController
@RequiredArgsConstructor
public class WalletController implements WalletSwagger {

    private final WalletService walletService;

    @Override
    public ResponseEntity<ApiResponse<WalletSummaryResponse>> getMyWalletSummary(CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        WalletSummaryResponse responseDto = walletService.getWalletSummary(email);

        return ResponseEntity.ok(ApiResponse.of(WALLET_SUMMARY_SUCCESS, responseDto));
    }
}
