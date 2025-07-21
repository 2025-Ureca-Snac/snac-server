package com.ureca.snac.trade.service.interfaces;

import com.ureca.snac.trade.controller.request.BuyerFilterRequest;

public interface BuyFilterService {
    void saveBuyerFilter(String username, BuyerFilterRequest filter);
}
