package com.ureca.snac.trade.service;

import com.ureca.snac.member.Member;
import com.ureca.snac.member.Role;
import com.ureca.snac.trade.dto.dispute.DisputeAnswerRequest;
import com.ureca.snac.trade.entity.*;
import com.ureca.snac.trade.exception.DisputeAdminPermissionDeniedException;
import com.ureca.snac.trade.exception.DisputeNotFoundException;
import com.ureca.snac.trade.repository.DisputeRepository;
import com.ureca.snac.trade.service.interfaces.DisputeAdminService;
import com.ureca.snac.trade.service.interfaces.PenaltyService;
import com.ureca.snac.trade.support.TradeSupport;
import com.ureca.snac.wallet.entity.Wallet;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class DisputeAdminServiceImpl implements DisputeAdminService {

    private final DisputeRepository disputeRepository;
    private final PenaltyService penaltyService;
    private final TradeSupport tradeSupport;

    @Override
    public void answer(Long id, DisputeAnswerRequest dto, String adminEmail) {
        assertAdmin(adminEmail); // 권한 확인

        Dispute dispute = disputeRepository.findById(id)
                .orElseThrow(DisputeNotFoundException::new);

        Trade trade = dispute.getTrade();

        if (dto.isNeedMore()) {
            dispute.needMore(dto.getAnswer()); // 상태 needMore 로 두고, answer 저장, answerAt 같이
            return;
        }

        // 최종처리, 환불 등
        dispute.answered(dto.getAnswer()); // 답변

        Wallet buyerWallet = tradeSupport.findLockedWallet(trade.getBuyer().getId());
        long refundMoney = trade.getPriceGb() - trade.getPoint();
        if (refundMoney > 0) buyerWallet.depositMoney(refundMoney);
        if (trade.getPoint() > 0) buyerWallet.depositPoint(trade.getPoint());

        trade.changeStatus(TradeStatus.CANCELED);
        trade.resumeAutoConfirm(); // 타이머 재개

        penaltyService.givePenalty(trade.getSeller().getEmail(), PenaltyReason.SELLER_FAULT);
    }

    private void assertAdmin(String email) {
        Member admin = tradeSupport.findMember(email);
        if (!admin.getRole().equals(Role.ADMIN))
            throw new DisputeAdminPermissionDeniedException();
    }
}