package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.dto.DisputeSearchCond;
import com.ureca.snac.trade.dto.dispute.DisputeAnswerRequest;
import com.ureca.snac.trade.entity.DisputeStatus;
import com.ureca.snac.trade.entity.DisputeType;
import com.ureca.snac.trade.service.interfaces.DisputeAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.ureca.snac.common.BaseCode.*;


@RestController
@RequestMapping("/admin/disputes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DisputeAdminController {

    private final DisputeAdminService disputeAdminService;


    // 처리
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<?>> answer(
            @PathVariable Long id,
            @RequestBody @Valid DisputeAnswerRequest disputeAnswerRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        disputeAdminService.answer(id, disputeAnswerRequest, userDetails.getUsername());

        BaseCode baseCode = switch (disputeAnswerRequest.getResult()) {
            case NEED_MORE -> DISPUTE_NEED_MORE;
            case REJECTED  -> DISPUTE_REJECTED_SUCCESS;
            default        -> DISPUTE_ANSWERED_SUCCESS;
        };

        return ResponseEntity.ok(ApiResponse.ok(baseCode));
    }

    // 전체 검색
    @GetMapping
    public ResponseEntity<ApiResponse<?>> search(
            @RequestParam(required=false) DisputeStatus status,
            @RequestParam(required=false) DisputeType type,
            @RequestParam(required=false) String reporter,
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size) {

        var cond = new DisputeSearchCond(status, type, reporter);
        var data = disputeAdminService.list(cond, PageRequest.of(page,size));
        return ResponseEntity.ok(ApiResponse.of(BaseCode.DISPUTE_DETAIL_SUCCESS, data));
    }

    // 처리 대기 신고
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<?>> pending(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size) {

        var cond = new DisputeSearchCond(DisputeStatus.IN_PROGRESS, null, null);
        var data = disputeAdminService.list(cond, PageRequest.of(page,size));
        return ResponseEntity.ok(ApiResponse.of(BaseCode.DISPUTE_DETAIL_SUCCESS, data));
    }

    // 상세
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> detail(@PathVariable Long id){
        return ResponseEntity.ok(ApiResponse.of(
                BaseCode.STATUS_OK, disputeAdminService.detail(id)));
    }

}