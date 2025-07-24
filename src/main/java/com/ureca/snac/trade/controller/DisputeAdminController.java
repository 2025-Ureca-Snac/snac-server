package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.dto.dispute.CommentCreateRequest;
import com.ureca.snac.trade.dto.dispute.DisputeResolveRequest;
import com.ureca.snac.trade.entity.AuthorType;
import com.ureca.snac.trade.service.interfaces.DisputeAdminService;
import com.ureca.snac.trade.service.interfaces.DisputeCommentService;
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
    private final DisputeCommentService disputeCommentService;


    // 추가 자료 요청
    @PatchMapping("/{id}/request-extra")
    public ResponseEntity<ApiResponse<?>> requestExtra(
            @PathVariable Long id,
            @RequestBody CommentCreateRequest commentCreateRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        disputeAdminService.requestExtra(id, userDetails.getUsername(), commentCreateRequest.getContent());
        return ResponseEntity.ok(ApiResponse.ok(DISPUTE_EXTRA_REQUESTED));
    }

    // 처리
    @PatchMapping("/{id}/resolve")
    public ResponseEntity<ApiResponse<?>> resolve(
            @PathVariable Long id,
            @RequestBody @Valid DisputeResolveRequest disputeResolveRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        disputeAdminService.resolve(id, disputeResolveRequest, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(BaseCode.DISPUTE_RESOLVE_SUCCESS));
    }

    // 관리자 답변
    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<?>> adminComment(
            @PathVariable Long id,
            @RequestPart("content") String content,
            @RequestPart(value="requestExtra", required=false) Boolean requestExtra, // 첨부파일 필요시
            @AuthenticationPrincipal UserDetails userDetails) {

        // requestExtra가 null이 아니고 true일 때 extra가 true
        boolean extra = Boolean.TRUE.equals(requestExtra);
        Long commentId = disputeCommentService.addComment(
                id, userDetails.getUsername(), content, AuthorType.ADMIN, extra
        );

        return ResponseEntity.ok(ApiResponse.of(BaseCode.DISPUTE_COMMENT_SUCCESS, commentId));
    }
}