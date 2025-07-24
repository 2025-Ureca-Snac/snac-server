package com.ureca.snac.trade.service.interfaces;

import com.ureca.snac.trade.dto.dispute.DisputeResolveRequest;

public interface DisputeAdminService {
    void requestExtra(Long disputeId, String adminEmail, String adminComment);
    void resolve(Long disputeId, DisputeResolveRequest dto, String adminEmail);
}