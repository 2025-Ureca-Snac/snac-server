package com.ureca.snac.finance.service;

import com.ureca.snac.finance.controller.request.CreateAccountRequest;
import com.ureca.snac.finance.controller.request.UpdateAccountRequest;
import com.ureca.snac.finance.entity.Account;
import com.ureca.snac.finance.entity.Bank;
import com.ureca.snac.finance.exception.AccountNotFoundException;
import com.ureca.snac.finance.exception.BankNotFoundException;
import com.ureca.snac.finance.repository.AccountRepository;
import com.ureca.snac.finance.repository.BankRepository;
import com.ureca.snac.finance.service.response.AccountResponse;
import com.ureca.snac.member.Member;
import com.ureca.snac.member.repository.MemberRepository;
import com.ureca.snac.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final BankRepository bankRepository;

    @Override
    @Transactional
    public Long createAccount(String username, CreateAccountRequest createAccountRequest) {
        Member member = getMember(username);
        Bank bank = bankRepository.findById(createAccountRequest.getBankId()).orElseThrow(BankNotFoundException::new);

        Account account = Account.builder()
                .member(member)
                .bank(bank)
                .accountNumber(createAccountRequest.getAccountNumber())
                .build();

        Account saved = accountRepository.save(account);

        return saved.getId();
    }

    @Override
    public List<AccountResponse> getAccounts(String username) {
        Member member = getMember(username);

        List<Account> accounts = accountRepository.findAllByMember(member);

        return accounts.stream()
                .map(AccountResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void updateAccount(String username, Long accountId, UpdateAccountRequest updateAccountRequest) {
        Member member = getMember(username);
        Bank bank = bankRepository.findById(updateAccountRequest.getBankId()).orElseThrow(BankNotFoundException::new);
        Account account = accountRepository.findByMember(member).orElseThrow(AccountNotFoundException::new);

        account.update(bank, updateAccountRequest.getAccountNumber());
    }

    @Override
    @Transactional
    public void deleteAccount(String username, Long accountId) {
        Member member = getMember(username);
        Account account = accountRepository.findByMember(member).orElseThrow(AccountNotFoundException::new);

        accountRepository.delete(account);
    }


    private Member getMember(String username) {
        return memberRepository.findByEmail(username).orElseThrow(MemberNotFoundException::new);
    }
}
