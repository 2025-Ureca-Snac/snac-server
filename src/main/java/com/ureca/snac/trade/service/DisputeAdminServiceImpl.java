package com.ureca.snac.trade.service;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;
import com.ureca.snac.common.s3.S3Uploader;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.Role;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.trade.dto.DisputeSearchCond;
import com.ureca.snac.trade.dto.dispute.DisputeAnswerRequest;
import com.ureca.snac.trade.dto.dispute.DisputeDetailResponse;
import com.ureca.snac.trade.entity.*;
import com.ureca.snac.trade.exception.DisputeAdminPermissionDeniedException;
import com.ureca.snac.trade.exception.DisputeNotFoundException;
import com.ureca.snac.trade.repository.DisputeAttachmentRepository;
import com.ureca.snac.trade.repository.DisputeRepository;
import com.ureca.snac.trade.service.interfaces.DisputeAdminService;
import com.ureca.snac.trade.service.interfaces.PenaltyService;
import com.ureca.snac.wallet.Repository.WalletRepository;
import com.ureca.snac.wallet.entity.Wallet;
import com.ureca.snac.wallet.exception.WalletNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DisputeAdminServiceImpl implements DisputeAdminService {

    private final DisputeRepository disputeRepository;
    private final MemberRepository memberRepository;
    private final WalletRepository walletRepository;
    private final DisputeAttachmentRepository disputeAttachmentRepository;
    private final PenaltyService penaltyService;
    private final S3Uploader s3;   // presigned URL 변환용

    private static final List<DisputeStatus> ACTIVE = List.of(DisputeStatus.IN_PROGRESS, DisputeStatus.NEED_MORE);

    @Override
    public void answer(Long id, DisputeAnswerRequest dto, String adminEmail) {
        assertAdmin(adminEmail); // 권한 확인

        Dispute dispute = disputeRepository.findById(id)
                .orElseThrow(DisputeNotFoundException::new);

        Trade trade = dispute.getTrade();

        switch (dto.getResult()) {
            case NEED_MORE -> {
                dispute.needMore(dto.getAnswer()); // 답변 상태 갱신
                // 자동확정은 안됨.. 고려해봐야 할 부분
            }
            case REJECTED  -> { // 신고 기각
                dispute.reject(dto.getAnswer());
                //  trade.resumeAutoConfirm(); // 자동확정 재개

                restoreTradeIfNoActive(trade);

                // 환불 패널티 x
            }
            case ANSWERED  -> {
                dispute.answered(dto.getAnswer());

//                // 최종처리, 환불 등
//                // 환불
//                Wallet buyerWallet = findLockedWallet(trade.getBuyer().getId());
//                long refundMoney = trade.getPriceGb() - trade.getPoint();
//                if (refundMoney > 0) buyerWallet.depositMoney(refundMoney);
//                if (trade.getPoint() > 0) buyerWallet.depositPoint(trade.getPoint());
//                // 거래 취소
//                trade.changeStatus(TradeStatus.CANCELED);
//                trade.resumeAutoConfirm(); // 타이머 재개
//                // 판매자 패널티
//                penaltyService.givePenalty(trade.getSeller().getEmail(), PenaltyReason.SELLER_FAULT);
            }
        }
    }



    // 목록 조회
    public Page<DisputeDetailResponse> list(DisputeSearchCond cond, Pageable page) {
        /* repository.search(...) 가 Page<Dispute> 를 주면
           map(this::toDto) 로 DTO 변환, 페이징 정보는 그대로 유지 */
        return disputeRepository.search(cond, page)
                .map(this::toDto);
    }

    // 관리자 답변 처리 후 , 활성 신고가 없으면 원상복구
    private void restoreTradeIfNoActive(Trade trade) {
        boolean hasActive = disputeRepository.existsByTradeIdAndStatusIn(trade.getId(), ACTIVE);
        if (hasActive) return;

        // REPORTED 로 만든 신고를 찾아 그때 백업했던 상태로 복귀
        disputeRepository.findTopByTradeIdAndReportedAppliedTrueOrderByCreatedAtAsc(trade.getId())
                .ifPresent(marker -> {
                    TradeStatus prev = marker.getPrevTradeStatus();
                    if (prev != null) {
                        trade.changeStatus(prev);
                    } else {
                        //  신고는 DATA_SENT 이후에 가능하므로 DATA_SENT로 복원
                        trade.changeStatus(TradeStatus.DATA_SENT);
                    }
                });

        trade.resumeAutoConfirm(); // 자동 확정 재개
    }

    /** 환불 + 거래 취소 + 자동확정 재개
     *    1) ANSWERED 상태의 Dispute 에서만 수행 (권한·검증 명확)
     *    2) 이미 CANCELED 인 거래면 중복 방지
     */
    @Override
    public void refundAndCancel(Long disputeId, String adminEmail) {
        assertAdmin(adminEmail);

        Dispute d = disputeRepository.findById(disputeId)
                .orElseThrow(DisputeNotFoundException::new);

        Trade trade = d.getTrade();

        // "답변 완료" 건에서만 환불/취소를 수행
        if (d.getStatus() != DisputeStatus.ANSWERED) {
            throw new BusinessException(BaseCode.DISPUTE_STATE_NOT_ANSWERED);
        }

        // 멱등성: 이미 취소면 차단
        if (trade.getStatus() == TradeStatus.CANCELED) {
            throw new BusinessException(BaseCode.TRADE_ALREADY_CANCELED);
        }

        // 환불 계산
        int priceGb = trade.getPriceGb() == null ? 0 : trade.getPriceGb();
        int pointUnit = trade.getPoint()   == null ? 0 : trade.getPoint();

        long refundMoney = Math.max(0, priceGb - pointUnit);
        long refundPoint = Math.max(0, pointUnit); // 포인트

        // 지갑 환불 (구매자)
        Wallet buyerWallet = findLockedWallet(trade.getBuyer().getId());
        if (refundMoney > 0) buyerWallet.depositMoney(refundMoney);
        if (refundPoint > 0) buyerWallet.depositPoint(refundPoint);

        // 거래 취소 + 자동확정 재개
        trade.changeStatus(TradeStatus.CANCELED);
        trade.resumeAutoConfirm();

    }

    // 판매자 패널티 부여
    @Override
    public void givePenaltyToSeller(Long disputeId, String adminEmail) {
        assertAdmin(adminEmail);

        Dispute d = disputeRepository.findById(disputeId)
                .orElseThrow(DisputeNotFoundException::new);

        String sellerEmail = d.getTrade().getSeller().getEmail();
        penaltyService.givePenalty(sellerEmail, PenaltyReason.SELLER_FAULT);
    }

    /** 활성 신고가 없으면 원상복구 + 자동확정 재개
     *  - NEED_MORE / IN_PROGRESS 가 0개일 때만 복구
     *  - 복구 기준: 최초 RE REPORTED 적용 Dispute.prevTradeStatus
     *  - 반환값: true = 복구됨 / false = 스킵
     */
    @Override
    public boolean finalizeIfNoActive(Long disputeId, String adminEmail) {
        assertAdmin(adminEmail);

        Dispute d = disputeRepository.findById(disputeId)
                .orElseThrow(DisputeNotFoundException::new);

        Trade trade = d.getTrade();

        boolean hasActive = disputeRepository.existsByTradeIdAndStatusIn(
                trade.getId(), ACTIVE);
        if (hasActive) return false;

        // 최초 REPORTED 적용자를 찾아 복원
        disputeRepository.findTopByTradeIdAndReportedAppliedTrueOrderByCreatedAtAsc(trade.getId())
                .ifPresent(marker -> {
                    TradeStatus prev = marker.getPrevTradeStatus();
                    trade.changeStatus(prev != null ? prev : TradeStatus.DATA_SENT);
                });

        trade.resumeAutoConfirm();
        return true;
    }


    // 엔티티 → DTO 변환 + 첨부 Presigned URL 생성
    private DisputeDetailResponse toDto(Dispute d) {
        List<String> urls = disputeAttachmentRepository.findByDispute(d)
                .stream()
                .map(a -> s3.generatePresignedUrl(a.getS3Key()))
                .toList();

        return new DisputeDetailResponse(
                d.getId(), d.getStatus(), d.getType(),
                d.getDescription(), d.getAnswer(),
                urls, d.getCreatedAt(), d.getAnswerAt());
    }

    private void assertAdmin(String email) {
        Member admin = findMember(email);
        if (!admin.getRole().equals(Role.ADMIN))
            throw new DisputeAdminPermissionDeniedException();
    }

    private Member findMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }

    private Wallet findLockedWallet(Long memberId) {
        return walletRepository.findByMemberIdWithLock(memberId).orElseThrow(WalletNotFoundException::new);
    }
}
