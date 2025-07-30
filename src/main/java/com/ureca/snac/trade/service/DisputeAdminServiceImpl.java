package com.ureca.snac.trade.service;

import com.ureca.snac.common.s3.S3Uploader;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.member.Role;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.trade.dto.DisputeSearchCond;
import com.ureca.snac.trade.dto.dispute.DisputeAnswerRequest;
import com.ureca.snac.trade.dto.dispute.DisputeDetailResponse;
import com.ureca.snac.trade.entity.Dispute;
import com.ureca.snac.trade.entity.PenaltyReason;
import com.ureca.snac.trade.entity.Trade;
import com.ureca.snac.trade.entity.TradeStatus;
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
                trade.resumeAutoConfirm(); // 자동확정 재개
                // 환불 패널티 x
            }
            case ANSWERED  -> {
                dispute.answered(dto.getAnswer());

                // 최종처리, 환불 등
                // 환불
                Wallet buyerWallet = findLockedWallet(trade.getBuyer().getId());
                long refundMoney = trade.getPriceGb() - trade.getPoint();
                if (refundMoney > 0) buyerWallet.depositMoney(refundMoney);
                if (trade.getPoint() > 0) buyerWallet.depositPoint(trade.getPoint());
                // 거래 취소
                trade.changeStatus(TradeStatus.CANCELED);
                trade.resumeAutoConfirm(); // 타이머 재개
                // 판매자 패널티
                penaltyService.givePenalty(trade.getSeller().getEmail(), PenaltyReason.SELLER_FAULT);
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
