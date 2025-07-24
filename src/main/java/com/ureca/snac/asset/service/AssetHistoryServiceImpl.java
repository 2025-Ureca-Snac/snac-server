package com.ureca.snac.asset.service;

import com.ureca.snac.asset.dto.AssetHistoryListRequest;
import com.ureca.snac.asset.dto.AssetHistoryResponse;
import com.ureca.snac.asset.entity.AssetHistory;
import com.ureca.snac.asset.entity.TransactionCategory;
import com.ureca.snac.asset.event.AssetChangedEvent;
import com.ureca.snac.asset.repository.AssetHistoryRepository;
import com.ureca.snac.common.CursorResult;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssetHistoryServiceImpl implements AssetHistoryService {

    private final MemberRepository memberRepository;
    private final AssetHistoryRepository assetHistoryRepository;
    private final PaymentRepository paymentRepository;

    /**
     * 트랜잭션 이벤트 리스너를 통해서
     * 이벤트 발행한 쪽의 트랜잭션이 커밋될 때만 실행. 데이터 정합성 보장
     * REQUIRES_NEW 를 통해 새 트랜잭션에서 실행하여,
     * 내역 기록이 실패하더라도 원래 트랜잭션에 영향주지 않도록 결리
     *
     * @param event 자산 변동 정보 이벤트
     */
    @Override
    @TransactionalEventListener // 언제 실행할지 메인작업 커밋후
    @Transactional(propagation = Propagation.REQUIRES_NEW) // 새 트랜잭션으로
    public void handleAssetChangedEvent(AssetChangedEvent event) {
        log.info("[자산 이벤트 수신] 자산 내역 기록 시작. 회원 ID : {}", event.memberId());

        try {
            Member member = memberRepository.findById(event.memberId())
                    .orElseThrow(MemberNotFoundException::new);

            AssetHistory history = AssetHistory.create(
                    member,
                    event.assetType(),
                    event.transactionType(),
                    event.category(),
                    event.amount(),
                    event.balanceAfter(),
                    event.title(),
                    event.sourceDomain(),
                    event.sourceId()
            );

            assetHistoryRepository.save(history);
            log.info("[자산 내역 기록 완료] 기록 저장 성공. historyId : {}, title : {}", history.getId(), history.getTitle());
        } catch (Exception e) {
            // 예외 다시 던져서 트랜잭션 롤백되게
            log.error("[자산 내역 기록 실패] 기록 중 심각한 예외 발생. 트랜잭션을 롤백 memberId : {}, event : {}",
                    event.memberId(), event, e);
            throw e;
        }
    }

    @Override
    public CursorResult<AssetHistoryResponse> getAssetHistories(
            String username, AssetHistoryListRequest request) {

        log.info("[자산 내역] 요청을 처리. 회원 : {}, 조건 : {}", username, request);

        Member member = findMemberByEmail(username);
        Slice<AssetHistory> historySlice =
                assetHistoryRepository.findWithFilters(member.getId(), request);

        List<AssetHistory> histories = historySlice.getContent();

        Map<Long, Payment> paymentMap = getPaymentMapForHistories(histories);
        List<AssetHistoryResponse> historyDtos = mapHistoriesToDtos(histories, paymentMap);

        String nextCursor = calculateNextCursor(historySlice);

        return new CursorResult<>(historyDtos, nextCursor, historySlice.hasNext());
    }

    private Map<Long, Payment> getPaymentMapForHistories(List<AssetHistory> histories) {
        List<Long> paymentIds = new ArrayList<>();
        for (AssetHistory history : histories) {
            if (history.getCategory() == TransactionCategory.RECHARGE) {
                paymentIds.add(history.getSourceId());
            }
        }
        if (paymentIds.isEmpty()) {
            return new HashMap<>();
        }
        List<Payment> payments = paymentRepository.findAllById(paymentIds);
        Map<Long, Payment> paymentMap = new HashMap<>();
        for (Payment payment : payments) {
            paymentMap.put(payment.getId(), payment);
        }
        return paymentMap;
    }

    private List<AssetHistoryResponse> mapHistoriesToDtos(
            List<AssetHistory> histories, Map<Long, Payment> paymentMap) {
        List<AssetHistoryResponse> historyResponses = new ArrayList<>();

        for (AssetHistory history : histories) {
            String paymentKey = null;
            if (history.getCategory() == TransactionCategory.RECHARGE) {
                Payment payment = paymentMap.get(history.getSourceId());
                if (payment != null) {
                    paymentKey = payment.getPaymentKey();
                }
            }
            historyResponses.add(AssetHistoryResponse.from(history, paymentKey));
        }
        return historyResponses;
    }

    private String calculateNextCursor(Slice<AssetHistory> historySlice) {
        if (!historySlice.hasNext() || historySlice.getContent().isEmpty()) {
            return null;
        }
        AssetHistory lastHistory =
                historySlice.getContent().get(historySlice.getContent().size() - 1);
        return lastHistory.getCreatedAt().toString() + "," + lastHistory.getId();
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);
    }
}
