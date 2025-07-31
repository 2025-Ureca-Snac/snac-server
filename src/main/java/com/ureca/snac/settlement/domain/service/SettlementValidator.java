package com.ureca.snac.settlement.domain.service;

import com.ureca.snac.finance.entity.Account;
import com.ureca.snac.finance.repository.AccountRepository;
import com.ureca.snac.member.entity.Member;
import com.ureca.snac.settlement.domain.exception.SettlementAccountMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SettlementValidator {

    private final AccountRepository accountRepository;

    public void validate(Member member, String inputAccountNumber) {

        // 모든 계좌를 가지고 온다음
        List<Account> allAccounts = accountRepository.findAllByMember(member);

        // 가져온 계좌목록중에 하나라도 일치하는게 있는지 확인
        // 엔티티에게 검증 위임
        boolean accountMatch = false;

        for (Account account : allAccounts) {
            if (account.isSameAccountNumber(inputAccountNumber)) {
                accountMatch = true;
                break;
            }
        }

        // 일치하는 게좌가 없음
        if (!accountMatch) {
            throw new SettlementAccountMismatchException();
        }
    }
}
