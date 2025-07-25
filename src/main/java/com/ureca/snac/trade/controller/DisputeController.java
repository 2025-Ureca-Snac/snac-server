package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.entity.DisputeType;
import com.ureca.snac.trade.service.interfaces.DisputeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/trades/{tradeId}/disputes")
@RequiredArgsConstructor
public class DisputeController {

    private final DisputeService disputeService;

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> createDispute(
            @PathVariable Long tradeId,
            @RequestPart("type") DisputeType type,
            @RequestPart(value="reason", required = false) String description,
            @RequestPart(value="files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long id = disputeService.createDispute(
                tradeId, userDetails.getUsername(), type, description, files == null ? List.of() : files
        );

        return ResponseEntity.ok(ApiResponse.of(BaseCode.DISPUTE_CREATE_SUCCESS, id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> detailDispute(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        return ResponseEntity.ok(ApiResponse.of(BaseCode.DISPUTE_DETAIL_SUCCESS,disputeService.getDispute(id, email)));
    }

}