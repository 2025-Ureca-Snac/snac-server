package com.ureca.snac.trade.service.interfaces;

import com.ureca.snac.trade.dto.dispute.DisputeDetailResponse;
import com.ureca.snac.trade.entity.DisputeType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DisputeService {
    Long createDispute(
            Long tradeId, String userEmail,
            DisputeType type, String reason,
            List<MultipartFile> files
    );
    DisputeDetailResponse getDispute(Long id, String requesterEmail);
}