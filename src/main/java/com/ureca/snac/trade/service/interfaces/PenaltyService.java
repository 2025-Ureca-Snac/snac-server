package com.ureca.snac.trade.service.interfaces;

import com.ureca.snac.trade.entity.PenaltyReason;

public interface PenaltyService {
    void givePenalty(String email, PenaltyReason reason);
}