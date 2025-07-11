package com.ureca.snac.finance.service.response;

import com.ureca.snac.finance.entity.Account;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountResponse {

    private Long id;
    private String bankName;
    private String accountNumber;

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getBank().getName(), account.getAccountNumber());
    }
}
