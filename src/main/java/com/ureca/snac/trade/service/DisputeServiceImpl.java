package com.ureca.snac.trade.service;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;
import com.ureca.snac.common.s3.S3Uploader;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.Role;
import com.ureca.snac.trade.dto.dispute.CommentResponse;
import com.ureca.snac.trade.dto.dispute.DisputeDetailResponse;
import com.ureca.snac.trade.entity.Dispute;
import com.ureca.snac.trade.entity.DisputeAttachment;
import com.ureca.snac.trade.entity.DisputeType;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.exception.DisputeNotFoundException;
import com.ureca.snac.trade.exception.DisputePermissionDeniedException;
import com.ureca.snac.trade.repository.DisputeAttachmentRepository;
import com.ureca.snac.trade.repository.DisputeCommentRepository;
import com.ureca.snac.trade.repository.DisputeRepository;
import com.ureca.snac.trade.service.interfaces.DisputeService;
import com.ureca.snac.trade.support.TradeSupport;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DisputeServiceImpl implements DisputeService {

    private final DisputeRepository disputeRepository;
    private final DisputeAttachmentRepository disputeAttachmentRepository;
    private final DisputeCommentRepository disputeCommentRepository;
    private final TradeSupport tradeSupport;
    private final S3Uploader s3Uploader;

    @Override
    public Long createDispute(Long tradeId, String email,
                              DisputeType type, String reason,
                              List<MultipartFile> files) {

        Trade trade = tradeSupport.findLockedTrade(tradeId);
        Member reporter = tradeSupport.findMember(email);

        // 거래 당사자 확인
        if (!reporter.equals(trade.getBuyer()) && !reporter.equals(trade.getSeller()))
            throw new DisputePermissionDeniedException();

        Dispute dispute = disputeRepository.save(
                Dispute.builder()
                        .trade(trade)
                        .reporter(reporter)
                        .type(type)
                        .reason(reason)
                        .build()
        );

        for (MultipartFile file : files) {
            String key = s3Uploader.upload(file, "disputes/attachments");
            disputeAttachmentRepository.save(new DisputeAttachment(dispute, key));
        }
        return dispute.getId();
    }

    @Override
    public DisputeDetailResponse getDispute(Long id, String email) {

        Dispute dispute = disputeRepository.findById(id)
                .orElseThrow(DisputeNotFoundException::new);

        Member reporter = tradeSupport.findMember(email);

        // 신고자 또는 관리자인지
        if (!reporter.equals(dispute.getReporter()) && !reporter.getRole().equals(Role.ADMIN))
            throw new DisputePermissionDeniedException();

        List<String> urls = disputeAttachmentRepository.findByDispute(dispute).stream()
                .map(attachment -> s3Uploader.generatePresignedUrl(attachment.getS3Key()))
                .toList();

        List<CommentResponse> comments = disputeCommentRepository.findByDisputeOrderByCreatedAtAsc(dispute).stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getAuthor(),
                        comment.getContent(),
                        comment.getCreatedAt()))
                .toList();

        return new DisputeDetailResponse(
                dispute.getId(),
                dispute.getStatus(),
                dispute.getType(),
                dispute.getReason(),
                urls,
                comments,
                dispute.getCreatedAt(),
                dispute.getResolvedAt());
    }
}