package com.ureca.snac.trade.service;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.Role;
import com.ureca.snac.trade.dto.dispute.DisputeResolveRequest;
import com.ureca.snac.trade.entity.AuthorType;
import com.ureca.snac.trade.entity.Dispute;
import com.ureca.snac.trade.entity.DisputeStatus;
import com.ureca.snac.trade.exception.DisputeAdminPermissionDeniedException;
import com.ureca.snac.trade.exception.DisputeNotFoundException;
import com.ureca.snac.trade.exception.DisputePermissionDeniedException;
import com.ureca.snac.trade.repository.DisputeRepository;
import com.ureca.snac.trade.service.interfaces.DisputeAdminService;
import com.ureca.snac.trade.service.interfaces.DisputeCommentService;
import com.ureca.snac.trade.service.interfaces.PenaltyService;
import com.ureca.snac.trade.support.TradeSupport;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class DisputeAdminServiceImpl implements DisputeAdminService {

    private final DisputeRepository disputeRepository;
    private final DisputeCommentService disputeCommentService;
    private final PenaltyService penaltyService;
    private final TradeSupport tradeSupport;

    @Override
    public void requestExtra(Long id, String adminEmail, String memo) {
        assertAdmin(adminEmail); // 권한 확인
        // 상태 in_review 에서 awaiting_user
        disputeCommentService.addComment(id, adminEmail, memo, AuthorType.ADMIN, true);

    }

    @Override
    public void resolve(Long id, DisputeResolveRequest dto, String adminEmail) {
        Dispute dispute = disputeRepository.findById(id)
                .orElseThrow(DisputeNotFoundException::new);

        assertAdmin(adminEmail); // 권한 확인

        if (dto.getResult() == DisputeStatus.RESOLVED) {
            // 환불 + 패널티(SELLER_FAULT) TODO
            dispute.resolve();
        } else if (dto.getResult() == DisputeStatus.REJECTED) {
            // 패널티(BUYER_FAULT) TODO
            dispute.reject();
        }
        // 관리자 처리 답변 저장
        if (dto.getAdminComment() != null) {
            disputeCommentService.addComment(id, adminEmail, dto.getAdminComment(), AuthorType.ADMIN, false);
        }
    }

    private void assertAdmin(String email) {
        Member admin = tradeSupport.findMember(email);
        if (!admin.getRole().equals(Role.ADMIN))
            throw new DisputeAdminPermissionDeniedException();
    }
}