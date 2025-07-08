package com.ureca.snac.finance.service;

import com.ureca.snac.finance.controller.request.CreateBankRequest;
import com.ureca.snac.finance.controller.request.UpdateBankRequest;
import com.ureca.snac.finance.service.response.BankResponse;

import java.util.List;

public interface BankService {

    Long createBank(CreateBankRequest createBankRequest);

    BankResponse getBankById(Long bankId);

    List<BankResponse> getAllBanks();

    void updateBank(Long bankId, UpdateBankRequest updateBankRequest);

    void deleteBank(Long bankId);
}
