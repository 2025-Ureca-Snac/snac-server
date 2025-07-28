package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.dto.dispute.DisputeCreateRequest;
import com.ureca.snac.trade.service.interfaces.DisputeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trades")
@RequiredArgsConstructor
public class DisputeController {

    private final DisputeService disputeService;

    @PostMapping("/{tradeId}/disputes")
    public ResponseEntity<ApiResponse<?>> createDispute(
            @PathVariable Long tradeId,
            @RequestBody @Valid DisputeCreateRequest disputeCreateRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long id = disputeService.createDispute(
                tradeId,
                userDetails.getUsername(),
                disputeCreateRequest.getType(),
                disputeCreateRequest.getDescription(),
                disputeCreateRequest.getAttachmentKeys()
        );

        return ResponseEntity.ok(ApiResponse.of(BaseCode.DISPUTE_CREATE_SUCCESS, id));
    }

    @GetMapping("/disputes/{id}")
    public ResponseEntity<ApiResponse<?>> detailDispute(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        return ResponseEntity.ok(ApiResponse.of(BaseCode.DISPUTE_DETAIL_SUCCESS,disputeService.getDispute(id, email)));
    }

}