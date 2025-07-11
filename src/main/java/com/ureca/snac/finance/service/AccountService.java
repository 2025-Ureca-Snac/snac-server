package com.ureca.snac.finance.service;

import com.ureca.snac.finance.controller.request.CreateAccountRequest;
import com.ureca.snac.finance.service.response.AccountResponse;

import java.util.List;

public interface AccountService {

    Long createAccount(String username, CreateAccountRequest createAccountRequest);

    List<AccountResponse> getAccounts(String username);
}
