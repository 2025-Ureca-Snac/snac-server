package com.ureca.snac.trade.service;

import com.ureca.snac.common.s3.S3Uploader;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.member.Role;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.trade.dto.dispute.DisputeDetailResponse;
import com.ureca.snac.trade.entity.Dispute;
import com.ureca.snac.trade.entity.DisputeAttachment;
import com.ureca.snac.trade.entity.DisputeType;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.exception.DisputeNotFoundException;
import com.ureca.snac.trade.exception.DisputePermissionDeniedException;
import com.ureca.snac.trade.exception.TradeNotFoundException;
import com.ureca.snac.trade.repository.DisputeAttachmentRepository;
import com.ureca.snac.trade.repository.DisputeRepository;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.DisputeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DisputeServiceImpl implements DisputeService {

    private final DisputeRepository disputeRepository;
    private final MemberRepository memberRepository;
    private final DisputeAttachmentRepository disputeAttachmentRepository;
    private final TradeRepository tradeRepository;
    private final S3Uploader s3Uploader;

    @Override
    public Long createDispute(Long tradeId, String email,
                              DisputeType type, String description,
                              List<String> attachmentKeys) {

        Trade trade = findLockedTrade(tradeId);
        Member reporter = findMember(email);

        // 거래 당사자 확인
        if (!reporter.equals(trade.getBuyer()) && !reporter.equals(trade.getSeller()))
            throw new DisputePermissionDeniedException();

        trade.pauseAutoConfirm();

        Dispute dispute = disputeRepository.save(
                Dispute.builder()
                        .trade(trade)
                        .reporter(reporter)
                        .type(type)
                        .description(description)
                        .build()
        );

        for (String key : attachmentKeys) {
            disputeAttachmentRepository.save(new DisputeAttachment(dispute, key));
        }
        return dispute.getId();
    }

    @Override
    public DisputeDetailResponse getDispute(Long id, String email) {

        Dispute dispute = disputeRepository.findById(id)
                .orElseThrow(DisputeNotFoundException::new);

        // 신고자 또는 관리자인지
        Member reporter = findMember(email);
        if (!reporter.equals(dispute.getReporter()) && !reporter.getRole().equals(Role.ADMIN))
            throw new DisputePermissionDeniedException();

        List<String> urls = disputeAttachmentRepository.findByDispute(dispute).stream()
                .map(attachment -> s3Uploader.generatePresignedUrl(attachment.getS3Key()))
                .toList();

        return new DisputeDetailResponse(
                dispute.getId(),
                dispute.getStatus(),
                dispute.getType(),
                dispute.getDescription(),
                dispute.getAnswer(),
                urls,
                dispute.getCreatedAt(),
                dispute.getAnswerAt());
    }

    private Member findMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    private Trade findLockedTrade(Long tradeId) {
        return tradeRepository.findLockedById(tradeId).orElseThrow(TradeNotFoundException::new);
    }
}
