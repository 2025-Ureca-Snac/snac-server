package com.ureca.snac.trade.service.interfaces;

import com.ureca.snac.trade.controller.request.BuyerFilterRequest;

import java.util.Map;

public interface BuyFilterService {
    void saveBuyerFilter(String username, BuyerFilterRequest filter);
    Map<String, BuyerFilterRequest> findAllBuyerFilters();
//    void deactivateBuyerFilterByUsername(String username);
    void deleteBuyerFilterByUsername(String username);
}
