package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.dto.DisputeSearchCond;
import com.ureca.snac.trade.dto.dispute.DisputeAnswerRequest;
import com.ureca.snac.trade.dto.dispute.DisputeDetailResponse;
import com.ureca.snac.trade.entity.Dispute;
import com.ureca.snac.trade.entity.DisputeStatus;
import com.ureca.snac.trade.entity.DisputeType;
import com.ureca.snac.trade.service.interfaces.DisputeAdminService;
import com.ureca.snac.trade.service.interfaces.DisputeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.ureca.snac.common.BaseCode.*;


@RestController
@RequestMapping("/api/admin/disputes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DisputeAdminController {

    private final DisputeAdminService disputeAdminService;
    private final DisputeService disputeService;



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

        DisputeSearchCond cond = new DisputeSearchCond(DisputeStatus.IN_PROGRESS, null, null);
        Page<DisputeDetailResponse> data = disputeAdminService.list(cond, PageRequest.of(page,size));
        return ResponseEntity.ok(ApiResponse.of(BaseCode.DISPUTE_DETAIL_SUCCESS, data));
    }

    // 상세
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> detailDispute(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        return ResponseEntity.ok(ApiResponse.of(BaseCode.DISPUTE_DETAIL_SUCCESS,disputeService.getDispute(id, email)));
    }

    /** 1) 환불 + 거래 취소 */
    @PostMapping("/{id}/refund-and-cancel")
    public ResponseEntity<ApiResponse<Void>> refundAndCancel(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails admin) {

        disputeAdminService.refundAndCancel(id, admin.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.DISPUTE_REFUND_AND_CANCEL_SUCCESS));
    }

    /** 2) 판매자 패널티 */
    @PostMapping("/{id}/penalty-seller")
    public ResponseEntity<ApiResponse<Void>> penaltySeller(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails admin) {

        disputeAdminService.givePenaltyToSeller(id, admin.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.DISPUTE_PENALTY_GIVEN));
    }

    /** 3) 복구 시도: 활성 신고 0개면 원상복구
     * 답변만하고 처리 스킵할때 사용
     * */
    @PostMapping("/{id}/finalize")
    public ResponseEntity<ApiResponse<Void>> finalizeIfNoActive(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails admin) {

        boolean restored = disputeAdminService.finalizeIfNoActive(id, admin.getUsername());
        BaseCode code = restored ? BaseCode.DISPUTE_FINALIZE_RESTORED
                : BaseCode.DISPUTE_FINALIZE_SKIPPED;
        return ResponseEntity.ok(ApiResponse.ok(code));
    }

}