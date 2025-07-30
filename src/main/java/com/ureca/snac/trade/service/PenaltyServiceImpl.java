package com.ureca.snac.trade.service;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;
import com.ureca.snac.member.Activated;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.repository.MemberRepository;
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

        penaltyRepository.save(
                Penalty.builder()
                        .member(member)
                        .reason(reason)
                        .level(PenaltyLevel.WARNING)   // 개별 위반은 '경고' 레벨로 기록
                        .build()
        );

        // 누적 건수 확인
        long total = penaltyRepository.countByMember(member);

        // 누적 기준으로 Member 제재 단계 결정
        if (total >= 5 && member.getActivated() != Activated.PERMANENT_BAN) {
            member.permanentBan();                         // 영구 정지
        } else if (total >= 3 && member.getActivated() == Activated.NORMAL) {
            member.suspendUntil(LocalDateTime.now().plusDays(7)); // 7일 정지
        }
    }
}