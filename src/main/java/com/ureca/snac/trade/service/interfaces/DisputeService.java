package com.ureca.snac.trade.service.interfaces;

import com.ureca.snac.trade.dto.dispute.DisputeDetailResponse;
import com.ureca.snac.trade.dto.dispute.MyDisputeListItemDto;
import com.ureca.snac.trade.dto.dispute.ReceivedDisputeListItemDto;
import com.ureca.snac.trade.entity.DisputeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DisputeService {
    Long createDispute(
            Long tradeId,
            String userEmail,
            DisputeType type,
            String description,
            List<String> attachmentKeys
    );
    DisputeDetailResponse getDispute(Long id, String requesterEmail);

    Page<MyDisputeListItemDto> listMyDisputes(String email, Pageable pageable);

    Page<ReceivedDisputeListItemDto> listDisputesAgainstMe(String email, Pageable pageable);
}