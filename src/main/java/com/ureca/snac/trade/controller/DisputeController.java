package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.dto.dispute.*;
import com.ureca.snac.trade.service.interfaces.DisputeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DisputeController implements DisputeControllerSwagger{

    private final DisputeService disputeService;

    @PostMapping("/trades/{tradeId}/disputes")
    public ResponseEntity<ApiResponse<?>> createDispute(
            @PathVariable Long tradeId,
            @RequestBody @Valid DisputeCreateRequest disputeCreateRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long id = disputeService.createDispute(
                tradeId,
                userDetails.getUsername(),
                disputeCreateRequest.getTitle(),
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

    // 내가 신고한 목록
    @GetMapping("/disputes/mine")
    public ResponseEntity<ApiResponse<?>> myDisputes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails me) {

        PageRequest pr = PageRequest.of(page, size);
        Page<MyDisputeListItemDto> data = disputeService.listMyDisputes(me.getUsername(), pr);
        return ResponseEntity.ok(ApiResponse.of(BaseCode.DISPUTE_MY_LIST_SUCCESS, data));
    }

    // 내가 신고받은 목록
    @GetMapping("/disputes/received")
    public ResponseEntity<ApiResponse<?>> receivedDisputes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails me) {

        PageRequest pr = PageRequest.of(page, size);
        Page<ReceivedDisputeListItemDto> data = disputeService.listDisputesAgainstMe(me.getUsername(), pr);
        return ResponseEntity.ok(ApiResponse.of(BaseCode.DISPUTE_RECEIVED_LIST_SUCCESS, data));
    }

    @PostMapping("/qna")
    public ResponseEntity<ApiResponse<?>> createQna(
            @RequestBody @Valid QnaCreateRequest qnaCreateRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long id = disputeService.createQna(
                qnaCreateRequest.getTitle(),
                userDetails.getUsername(),
                qnaCreateRequest.getType(),
                qnaCreateRequest.getDescription(),
                qnaCreateRequest.getAttachmentKeys()
        );
        return ResponseEntity.ok(ApiResponse.of(BaseCode.QNA_CREATE_SUCCESS, id));
    }

}
