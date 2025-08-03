package com.ureca.snac.trade.service;

import com.ureca.snac.common.s3.S3Uploader;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.member.Role;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.trade.dto.dispute.DisputeDetailResponse;
import com.ureca.snac.trade.dto.dispute.MyDisputeListItemDto;
import com.ureca.snac.trade.dto.dispute.ReceivedDisputeListItemDto;
import com.ureca.snac.trade.dto.dispute.TradeSummaryDto;
import com.ureca.snac.trade.entity.*;
import com.ureca.snac.trade.exception.DisputeNotFoundException;
import com.ureca.snac.trade.exception.DisputePermissionDeniedException;
import com.ureca.snac.trade.exception.TradeNotFoundException;
import com.ureca.snac.trade.repository.DisputeAttachmentRepository;
import com.ureca.snac.trade.repository.DisputeRepository;
import com.ureca.snac.trade.repository.TradeRepository;
import com.ureca.snac.trade.service.interfaces.DisputeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Long createDispute(Long tradeId, String email, String title,
                              DisputeType type, String description,
                              List<String> attachmentKeys) {

        Trade trade = findLockedTrade(tradeId);
        Member reporter = findMember(email);

        // 거래 당사자 확인
        if (!reporter.equals(trade.getBuyer()) && !reporter.equals(trade.getSeller()))
            throw new DisputePermissionDeniedException();

        trade.pauseAutoConfirm();

        // 거래 최초 신고인지 확인
        boolean applied = false;
        TradeStatus prev = null;
        if (trade.getStatus() != TradeStatus.REPORTED) {
            prev = trade.getStatus(); // 백업
            trade.changeStatus(TradeStatus.REPORTED);
            applied = true; // 내가 상태 전환을 적용했다
        }


        Dispute dispute = disputeRepository.save(
                Dispute.builder()
                        .title(title)
                        .trade(trade)
                        .reporter(reporter)
                        .type(type)
                        .description(description)
                        .prevTradeStatus(prev)
                        .reportedApplied(applied)
                        .category(DisputeCategory.REPORT)
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

        String reporterNickname = dispute.getReporter() != null
                ? dispute.getReporter().getNickname() : null;

        String opponentNickname = null;
        if (dispute.getTrade() != null) {
            Trade t = dispute.getTrade();
            if (t.getBuyer() != null && t.getSeller() != null) {
                if (t.getBuyer().equals(dispute.getReporter())) {
                    opponentNickname = t.getSeller().getNickname();
                } else if (t.getSeller().equals(dispute.getReporter())) {
                    opponentNickname = t.getBuyer().getNickname();
                }
            }
        }

        return new DisputeDetailResponse(
                dispute.getId(),
                dispute.getStatus(),
                dispute.getType(),
                dispute.getTitle(),
                dispute.getDescription(),
                dispute.getAnswer(),
                dispute.getCategory(),
                urls,
                dispute.getCreatedAt(),
                dispute.getAnswerAt(),
                reporterNickname,
                opponentNickname
        );
    }

    // 내가 신고한 목록 (설명 포함)
    public Page<MyDisputeListItemDto> listMyDisputes(String email, Pageable pageable) {
        Member me = findMember(email);
        return disputeRepository.findByReporterOrderByCreatedAtDesc(me, pageable)
                .map(dispute -> {
                    TradeSummaryDto summary = null;
                    if (dispute.getTrade() != null) {
                        summary = toTradeSummary(dispute.getTrade(), me);
                    }
                    return new MyDisputeListItemDto(
                            dispute.getId(),
                            dispute.getStatus(),
                            dispute.getType(),
                            dispute.getTitle(),
                            dispute.getDescription(),
                            dispute.getCategory(),
                            dispute.getCreatedAt(),
                            dispute.getAnswerAt(),
                            summary
                    );
                });
    }

    // 신고받은 목록
    public Page<ReceivedDisputeListItemDto> listDisputesAgainstMe(String email, Pageable pageable) {
        Member me = findMember(email);
        return disputeRepository.findReceivedByParticipant(me, pageable)
                .map(dispute -> new ReceivedDisputeListItemDto(
                        dispute.getId(),
                        dispute.getStatus(),
                        dispute.getType(),
                        dispute.getCreatedAt(),
                        toTradeSummary(dispute.getTrade(), me)
                ));
    }

    private TradeSummaryDto toTradeSummary(Trade trade, Member me) {
        boolean meIsBuyer = me.equals(trade.getBuyer());
        Member counter = meIsBuyer ? trade.getSeller() : trade.getBuyer(); // 상대방
        return new TradeSummaryDto(
                trade.getId(),
                trade.getStatus(),
                trade.getTradeType(),
                trade.getPriceGb(),
                trade.getDataAmount(),
                trade.getCarrier().name(),
                meIsBuyer ? "BUYER" : "SELLER",
                counter != null ? counter.getId() : null
        );
    }

    @Override
    public Long createQna(String title, String email,
                          DisputeType type, String description,
                          List<String> attachmentKeys) {
        Member reporter = findMember(email);

        // QnA 문의는 trade 없이 생성
        Dispute dispute = disputeRepository.save(
                Dispute.builder()
                        .title(title)
                        .trade(null)
                        .reporter(reporter)
                        .type(type)
                        .description(description)
                        .prevTradeStatus(null)
                        .reportedApplied(false)
                        .category(DisputeCategory.QNA)
                        .build()
        );

        // 첨부파일 저장
        if (attachmentKeys != null) {
            for (String key : attachmentKeys) {
                disputeAttachmentRepository.save(new DisputeAttachment(dispute, key));
            }
        }
        return dispute.getId();
    }

    private Member findMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    private Trade findLockedTrade(Long tradeId) {
        return tradeRepository.findLockedById(tradeId).orElseThrow(TradeNotFoundException::new);
    }
}
