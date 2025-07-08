package com.ureca.snac.finance.service;

import com.ureca.snac.finance.controller.request.CreateBankRequest;
import com.ureca.snac.finance.controller.request.UpdateBankRequest;
import com.ureca.snac.finance.entity.Bank;
import com.ureca.snac.finance.exception.BankNotFoundException;
import com.ureca.snac.finance.repository.BankRepository;
import com.ureca.snac.finance.service.response.BankResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BankServiceImpl implements BankService{

    private final BankRepository bankRepository;

    @Override
    @Transactional
    public Long createBank(CreateBankRequest createBankRequest) {
        Bank savedBank = bankRepository.save(new Bank(createBankRequest.getName()));

        return savedBank.getId();
    }

    @Override
    public BankResponse getBankById(Long bankId) {
        return bankRepository.findById(bankId)
                .map(BankResponse::from)
                .orElseThrow(BankNotFoundException::new);
    }

    @Override
    public List<BankResponse> getAllBanks() {
        return bankRepository.findAll()
                .stream()
                .map(BankResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public void updateBank(Long bankId, UpdateBankRequest updateBankRequest) {
        Bank bank = bankRepository
                .findById(bankId)
                .orElseThrow(BankNotFoundException::new);

        bank.update(updateBankRequest.getName());
    }

    @Override
    @Transactional
    public void deleteBank(Long bankId) {
        bankRepository.deleteById(bankId);
    }
}
