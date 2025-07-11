package com.ureca.snac.finance.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.finance.controller.request.CreateBankRequest;
import com.ureca.snac.finance.controller.request.UpdateBankRequest;
import com.ureca.snac.finance.service.BankService;
import com.ureca.snac.finance.service.response.BankResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ureca.snac.common.BaseCode.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/banks")
public class BankController implements BankControllerSwagger {

    private final BankService bankService;

    @Override
    @GetMapping("/{bankId}")
    public ResponseEntity<ApiResponse<BankResponse>> getBank(@PathVariable("bankId") Long bankId) {
        return ResponseEntity.ok(ApiResponse.of(BANK_READ_SUCCESS, bankService.getBankById(bankId)));
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<BankResponse>>> getAllBanks() {
        return ResponseEntity.ok(ApiResponse.of(BANK_LIST_SUCCESS, bankService.getAllBanks()));
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createBank(@Validated @RequestBody CreateBankRequest createBankRequest) {
        bankService.createBank(createBankRequest);

        return ResponseEntity
                .status(BANK_CREATE_SUCCESS.getStatus())
                .body(ApiResponse.ok(BANK_CREATE_SUCCESS));
    }

    @Override
    @DeleteMapping("/{bankId}")
    public ResponseEntity<ApiResponse<?>> deleteBank(@PathVariable("bankId") Long bankId) {
        bankService.deleteBank(bankId);

        return ResponseEntity.ok(ApiResponse.ok(BANK_DELETE_SUCCESS));
    }

    @PatchMapping("/{bankId}")
    public ResponseEntity<ApiResponse<?>> updateBank(@PathVariable("bankId") Long bankId,
                                                     @Validated @RequestBody UpdateBankRequest updateBankRequest) {
        bankService.updateBank(bankId, updateBankRequest);

        return ResponseEntity.ok(ApiResponse.ok(BANK_UPDATE_SUCCESS));
    }
}
