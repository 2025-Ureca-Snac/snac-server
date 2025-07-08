package com.ureca.snac.finance.service.response;

import com.ureca.snac.finance.entity.Bank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BankResponse {

    private Long id;
    private String name;

    public static BankResponse from(Bank bank) {
        return new BankResponse(
                bank.getId(),
                bank.getName()
        );
    }
}
