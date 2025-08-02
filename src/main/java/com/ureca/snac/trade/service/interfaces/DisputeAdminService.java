package com.ureca.snac.trade.service.interfaces;

import com.ureca.snac.trade.dto.DisputeSearchCond;
import com.ureca.snac.trade.dto.dispute.DisputeAnswerRequest;
import com.ureca.snac.trade.dto.dispute.DisputeDetailResponse;
import com.ureca.snac.trade.dto.dispute.DisputeStatisticsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DisputeAdminService {
    void answer(Long id, DisputeAnswerRequest dto, String adminEmail);

    Page<DisputeDetailResponse> searchList(DisputeSearchCond cond, Pageable page);

    // 관리 동작 분리
    void refundAndCancel(Long disputeId, String adminEmail);
    void givePenaltyToSeller(Long disputeId, String adminEmail);
    boolean restoreIfNoActive(Long disputeId, String adminEmail);

    DisputeStatisticsResponse getStatistics();
}