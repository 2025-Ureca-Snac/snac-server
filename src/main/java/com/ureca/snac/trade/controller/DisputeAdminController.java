package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.dto.dispute.DisputeAnswerRequest;
import com.ureca.snac.trade.service.interfaces.DisputeAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static com.ureca.snac.common.BaseCode.*;


@RestController
@RequestMapping("/admin/disputes")
@RequiredArgsConstructor
public class DisputeAdminController {

    private final DisputeAdminService disputeAdminService;

    // 처리
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<?>> answer(
            @PathVariable Long id,
            @RequestBody @Valid DisputeAnswerRequest disputeAnswerRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        disputeAdminService.answer(id, disputeAnswerRequest, userDetails.getUsername());

        BaseCode baseCode = disputeAnswerRequest.isNeedMore()
                ?  DISPUTE_NEED_MORE : DISPUTE_ANSWERED_SUCCESS;

        return ResponseEntity.ok(ApiResponse.ok(baseCode));
    }

}