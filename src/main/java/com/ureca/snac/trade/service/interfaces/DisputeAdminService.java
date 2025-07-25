package com.ureca.snac.trade.service.interfaces;

import com.ureca.snac.trade.dto.dispute.DisputeAnswerRequest;

public interface DisputeAdminService {
    void answer(Long id, DisputeAnswerRequest dto, String adminEmail);
}