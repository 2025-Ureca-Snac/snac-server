package com.ureca.snac.board.controller;

import com.ureca.snac.board.service.response.*;
import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.swagger.annotation.error.ErrorCode400;
import com.ureca.snac.swagger.annotation.error.ErrorCode401;
import com.ureca.snac.swagger.annotation.error.ErrorCode404;
import com.ureca.snac.swagger.annotation.response.ApiCreatedResponse;
import com.ureca.snac.swagger.annotation.response.ApiSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "게시글 관리", description = "게시글 등록, 조회, 수정, 삭제, 목록(무한스크롤) 기능 제공")
@SecurityRequirement(name = "Authorization")
public interface ArticleControllerSwagger {

    @Operation(
            summary = "게시글 등록",
            description = """
                새로운 게시글을 등록합니다.
                - 제목, 본문 파일, 이미지 파일을 모두 첨부해야 합니다.
                - 파일은 multipart/form-data로 전송해야 합니다.
            """
    )
    @ApiCreatedResponse(description = "등록 성공")
    @ErrorCode400(description = "등록 실패 - 입력값이 잘못되었습니다.")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiResponse<CreateArticleResponse>> addArticle(
            @Parameter(description = "게시글 제목") @RequestParam String title,
            @Parameter(description = "게시글 본문 파일(.md 등)") @RequestPart MultipartFile file,
            @Parameter(description = "게시글 대표 이미지 파일") @RequestPart MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails);

    @Operation(
            summary = "게시글 수정",
            description = """
                기존 게시글의 정보를 수정합니다.
                - 제목, 본문 파일, 이미지 파일을 모두 새로 첨부해야 합니다.
                - 기존 파일은 삭제 후 새 파일로 교체됩니다.
            """
    )
    @ApiSuccessResponse(description = "수정 성공")
    @ErrorCode400(description = "수정 실패 - 입력값이 잘못되었습니다.")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode404(description = "존재하지 않는 게시글 ID")
    @PutMapping(value = "/{articleId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<ApiResponse<UpdateArticleResponse>> editArticle(
            @Parameter(description = "수정할 게시글 ID") @PathVariable("articleId") Long articleId,
            @Parameter(description = "게시글 제목") @RequestParam String title,
            @Parameter(description = "게시글 본문 파일(.md 등)") @RequestPart MultipartFile file,
            @Parameter(description = "게시글 대표 이미지 파일") @RequestPart MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails);

    @Operation(
            summary = "게시글 삭제",
            description = "등록된 게시글을 삭제합니다."
    )
    @ApiSuccessResponse(description = "삭제 성공")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @ErrorCode404(description = "삭제 실패 - 존재하지 않는 게시글 ID")
    @DeleteMapping("/{articleId}")
    ResponseEntity<ApiResponse<?>> removeArticle(@Parameter(description = "삭제할 게시글 ID") @PathVariable("articleId") Long articleId,
                                                 @AuthenticationPrincipal UserDetails userDetails);

    @Operation(
            summary = "게시글 상세 조회",
            description = "게시글 ID를 통해 등록된 게시글의 상세 정보를 조회합니다."
    )
    @ApiSuccessResponse(description = "게시글 상세 정보 조회 성공")
    @ErrorCode404(description = "조회 실패 - 존재하지 않는 게시글 ID")
    @GetMapping("/{articleId}")
    ResponseEntity<ApiResponse<ArticleResponse>> getArticle(
            @Parameter(description = "게시글 ID") @PathVariable("articleId") Long articleId);

    @Operation(
            summary = "게시글 목록 조회 (무한 스크롤)",
            description = """
                게시글을 커서 방식(무한 스크롤)으로 조회합니다.
                - lastArticleId: 마지막으로 조회한 게시글 ID(커서, 생략 시 최신부터)
                - size: 한 번에 조회할 개수(기본값 9)
                - hasNext: 추가 데이터 존재 여부 반환
            """
    )
    @ApiSuccessResponse(description = "목록 조회 성공")
    @ErrorCode400(description = "조회 실패 - 잘못된 요청 파라미터")
    @GetMapping
    ResponseEntity<ApiResponse<ListArticleResponse>> getArticles(
            @Parameter(description = "마지막으로 조회한 게시글 ID(커서)") @RequestParam(required = false) Long lastArticleId,
            @Parameter(description = "페이지 당 조회 개수", example = "9") @RequestParam(defaultValue = "9") Integer size
    );

    @Operation(
            summary = "게시글 개수 조회",
            description = "전체 게시글 수를 반환합니다."
    )
    @ApiSuccessResponse(description = "게시글 개수 조회 성공")
    @ErrorCode401(description = "인증되지 않은 사용자 접근")
    @GetMapping("/count")
    ResponseEntity<ApiResponse<CountArticleResponse>> countArticles(@AuthenticationPrincipal UserDetails userDetails);
}
