package com.ureca.snac.asset.service;

import com.ureca.snac.asset.dto.AssetHistoryListRequest;
import com.ureca.snac.asset.dto.AssetHistoryResponse;
import com.ureca.snac.asset.entity.AssetHistory;
import com.ureca.snac.asset.entity.TransactionCategory;
import com.ureca.snac.asset.repository.AssetHistoryRepository;
import com.ureca.snac.common.CursorResult;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.member.exception.MemberNotFoundException;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.payment.entity.Payment;
import com.ureca.snac.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
