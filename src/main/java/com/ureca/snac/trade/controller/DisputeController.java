package com.ureca.snac.trade.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.common.BaseCode;
import com.ureca.snac.trade.dto.dispute.CommentCreateRequest;
import com.ureca.snac.trade.dto.dispute.CommentResponse;
import com.ureca.snac.trade.dto.dispute.DisputeDetailResponse;
import com.ureca.snac.trade.entity.AuthorType;
import com.ureca.snac.trade.entity.DisputeType;
import com.ureca.snac.trade.service.interfaces.DisputeCommentService;
import com.ureca.snac.trade.service.interfaces.DisputeService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final DisputeCommentService commentService;

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> createDispute(
            @PathVariable Long tradeId,
            @RequestPart("type") DisputeType type,
            @RequestPart(value="reason", required = false) String reason,
            @RequestPart(value="files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long id = disputeService.createDispute(
                tradeId, userDetails.getUsername(), type, reason, files == null ? List.of() : files
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

    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<Long>> createComment(
            @PathVariable Long id,
            @RequestBody @Valid CommentCreateRequest req,
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername();
        Long commentId = commentService.addComment(id, email, req.getContent(), AuthorType.USER, false);
        return ResponseEntity.ok(ApiResponse.of(BaseCode.DISPUTE_COMMENT_SUCCESS, commentId));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> commentList(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(ApiResponse.of(
                BaseCode.DISPUTE_COMMENT_READ_SUCCESS, commentService.listComment(id, userDetails.getUsername()))
        );
    }
}