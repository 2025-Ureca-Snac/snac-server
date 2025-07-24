package com.ureca.snac.trade.service;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.Role;
import com.ureca.snac.trade.dto.dispute.CommentResponse;
import com.ureca.snac.trade.entity.AuthorType;
import com.ureca.snac.trade.entity.Dispute;
import com.ureca.snac.trade.entity.DisputeComment;
import com.ureca.snac.trade.entity.DisputeStatus;
import com.ureca.snac.trade.exception.DisputeCommentPermissionDeniedException;
import com.ureca.snac.trade.exception.DisputeNotFoundException;
import com.ureca.snac.trade.exception.DisputePermissionDeniedException;
import com.ureca.snac.trade.repository.DisputeCommentRepository;
import com.ureca.snac.trade.repository.DisputeRepository;
import com.ureca.snac.trade.service.interfaces.DisputeCommentService;
import com.ureca.snac.trade.support.TradeSupport;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DisputeCommentServiceImpl implements DisputeCommentService {

    private final DisputeRepository disputeRepository;
    private final DisputeCommentRepository disputeCommentRepository;
    private final TradeSupport tradeSupport;

    @Override
    public Long addComment(Long disputeId, String email,
                           String content, AuthorType author, boolean requestExtra) {

        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(DisputeNotFoundException::new);
        Member writer = tradeSupport.findMember(email);

        // 권한 췤
        if (!writer.equals(dispute.getReporter()) && !writer.getRole().equals(Role.ADMIN))
            throw new DisputeCommentPermissionDeniedException();

        DisputeComment disputeComment = disputeCommentRepository.save(
                new DisputeComment(dispute, writer, author, content)
        );

        // 상태 변경
        if (author == AuthorType.ADMIN && dispute.getStatus() == DisputeStatus.IN_REVIEW) {
            dispute.awaitingUser(); // 관리자가 작성하면 추가 첨부나 답변 요청
        } else if (author == AuthorType.USER && dispute.getStatus() == DisputeStatus.AWAITING_USER) {
            dispute.inReview(); // 사용자가 글 올리면 처리 중으로 변경
        }
        switch (dispute.getStatus()) {
            case OPEN -> {
                // 최초 접수 직후 답변
                if (author == AuthorType.ADMIN) dispute.awaitingUser();
                // USER가 바로 자료를 첨부설명하는 상황이면 IN_REVIEW
                else dispute.inReview();
            }
            case IN_REVIEW -> {
                if (author == AuthorType.ADMIN && requestExtra) dispute.awaitingUser();
            }
            case AWAITING_USER -> {
                if (author == AuthorType.ADMIN) dispute.inReview();
            }
            // RESOLVED/REJECTED는 댓글만 추가, 상태 고정
        }

        return disputeComment.getId();
    }

    @Override
    public List<CommentResponse> listComment(Long disputeId, String email) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(DisputeNotFoundException::new);


        // 권한 체크
        Member member = tradeSupport.findMember(email);
        if (!member.equals(dispute.getReporter()) && !member.getRole().equals(Role.ADMIN))
            throw new DisputePermissionDeniedException();


        return disputeCommentRepository.findByDisputeOrderByCreatedAtAsc(dispute).stream()
                .map(disputeComment -> new CommentResponse(
                        disputeComment.getId(),
                        disputeComment.getAuthor(),
                        disputeComment.getContent(),
                        disputeComment.getCreatedAt())
                )
                .toList();
    }
}