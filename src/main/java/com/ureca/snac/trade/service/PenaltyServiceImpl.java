package com.ureca.snac.trade.service;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.trade.entity.Penalty;
import com.ureca.snac.trade.entity.PenaltyLevel;
import com.ureca.snac.trade.entity.PenaltyReason;
import com.ureca.snac.trade.repository.PenaltyRepository;
import com.ureca.snac.trade.service.interfaces.PenaltyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PenaltyServiceImpl implements PenaltyService {

    private final PenaltyRepository penaltyRepository;
    private final MemberRepository memberRepository;

    @Override
    public void givePenalty(String email, PenaltyReason reason) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BaseCode.MEMBER_NOT_FOUND));

        Penalty last = penaltyRepository.findTopByMemberOrderByCreatedAtDesc(member)
                .orElse(null);


        long total = penaltyRepository.countByMember(member) + 1; // 현재 건 포함


        PenaltyLevel next;
        if (total >= 5) {
            next = PenaltyLevel.BAN;
        } else if (total >= 3) {
            next = PenaltyLevel.SUSPEND;
        } else {
            next = PenaltyLevel.WARNING; // 경고
        }

        // 직전과 다른 레벨일 때만 새 레코드 저장
        if (last == null || last.getLevel() != next) {
            penaltyRepository.save(
                    Penalty.builder()
                            .member(member)
                            .reason(reason)
                            .level(next)
                            .build()
            );

            // Member 상태 업데이트
            if (next == PenaltyLevel.SUSPEND) {
                member.suspendUntil(LocalDateTime.now().plusDays(7));
            } else if (next == PenaltyLevel.BAN) {
                member.permanentBan();
            }
        }
    }
}