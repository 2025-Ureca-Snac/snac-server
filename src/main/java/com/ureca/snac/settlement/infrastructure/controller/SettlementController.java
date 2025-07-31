package com.ureca.snac.settlement.infrastructure.controller;

import com.ureca.snac.auth.dto.CustomUserDetails;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.settlement.application.dto.SettlementRequest;
import com.ureca.snac.settlement.application.service.SettlementService;
import com.ureca.snac.swagger.annotation.UserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ureca.snac.common.BaseCode.SETTLEMENT_SUCCESS;

@RestController
@RequestMapping("/api/settlements")
@RequiredArgsConstructor
public class SettlementController implements SettlementSwagger {

    private final SettlementService settlementService;

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createSettlement(
            @UserInfo CustomUserDetails userDetails,
            @Valid @RequestBody SettlementRequest request) {
        String username = userDetails.getUsername();
        settlementService.processSettlement(
                username, request.amount(), request.accountNumber()
        );

        return ResponseEntity.ok(ApiResponse.ok(SETTLEMENT_SUCCESS));
    }
}
