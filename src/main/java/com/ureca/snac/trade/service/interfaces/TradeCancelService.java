package com.ureca.snac.trade.service.interfaces;

import com.ureca.snac.trade.entity.CancelReason;

public interface TradeCancelService {
    void requestCancel(Long tradeId, String username, CancelReason reason);
    void acceptCancel(Long tradeId, String username);
    void rejectCancel(Long tradeId, String username);
}