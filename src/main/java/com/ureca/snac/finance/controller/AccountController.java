package com.ureca.snac.finance.controller;

import com.ureca.snac.common.ApiResponse;
import com.ureca.snac.finance.controller.request.CreateAccountRequest;
import com.ureca.snac.finance.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.ureca.snac.common.BaseCode.ACCOUNT_CREATE_SUCCESS;
import static com.ureca.snac.common.BaseCode.ACCOUNT_LIST_SUCCESS;

@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController implements AccountControllerSwagger {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createAccount(@Validated @RequestBody CreateAccountRequest createAccountRequest, @AuthenticationPrincipal UserDetails userDetails) {
        accountService.createAccount(userDetails.getUsername(), createAccountRequest);

        return ResponseEntity.status(ACCOUNT_CREATE_SUCCESS.getStatus())
                .body(ApiResponse.ok(ACCOUNT_CREATE_SUCCESS));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAccounts(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity
                .ok(ApiResponse.of(ACCOUNT_LIST_SUCCESS, accountService.getAccounts(userDetails.getUsername())));
    }
}
