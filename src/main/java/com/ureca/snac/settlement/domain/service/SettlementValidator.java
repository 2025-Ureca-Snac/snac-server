package com.ureca.snac.settlement.domain.service;

import com.ureca.snac.finance.entity.Account;
import com.ureca.snac.finance.exception.AccountNotFoundException;
import com.ureca.snac.finance.repository.AccountRepository;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.settlement.domain.exception.SettlementAccountMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SettlementValidator {

    private final AccountRepository accountRepository;

    public void validate(Member member, String inputAccountNumber) {
        Account registeredAccount = accountRepository.findByMember(member)
                .orElseThrow(AccountNotFoundException::new);

        if (!registeredAccount.isSameAccountNumber(inputAccountNumber))
            throw new SettlementAccountMismatchException();
    }
}
