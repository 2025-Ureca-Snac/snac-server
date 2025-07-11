package com.ureca.snac.finance.service;

import com.ureca.snac.finance.controller.request.CreateAccountRequest;
import com.ureca.snac.finance.controller.request.UpdateAccountRequest;
import com.ureca.snac.finance.exception.AccountNotFoundException;
import com.ureca.snac.finance.exception.BankNotFoundException;
import com.ureca.snac.finance.service.response.AccountResponse;
import com.ureca.snac.member.exception.MemberNotFoundException;

import java.util.List;

public interface AccountService {

    /**
     * 사용자의 계좌를 생성합니다.
     *
     * @param username 로그인한 사용자의 이메일
     * @param createAccountRequest 계좌 생성 요청 정보 (은행 ID, 계좌번호 등)
     * @return 생성된 계좌의 ID
     * @throws MemberNotFoundException 사용자를 찾을 수 없는 경우
     * @throws BankNotFoundException 은행 정보를 찾을 수 없는 경우
     */
    Long createAccount(String username, CreateAccountRequest createAccountRequest);

    /**
     * 사용자의 모든 계좌 정보를 조회합니다.
     *
     * @param username 로그인한 사용자의 이메일
     * @return 계좌 응답 리스트
     * @throws MemberNotFoundException 사용자를 찾을 수 없는 경우
     */
    List<AccountResponse> getAccounts(String username);

    /**
     * 사용자의 계좌 정보를 수정합니다.
     *
     * @param username 로그인한 사용자의 이메일
     * @param accountId 수정할 계좌의 ID
     * @param updateAccountRequest 수정 요청 정보 (은행 ID, 계좌번호 등)
     * @throws MemberNotFoundException 사용자를 찾을 수 없는 경우
     * @throws BankNotFoundException 은행 정보를 찾을 수 없는 경우
     * @throws AccountNotFoundException 계좌를 찾을 수 없는 경우
     */
    void updateAccount(String username, Long accountId, UpdateAccountRequest updateAccountRequest);

    /**
     * 사용자의 계좌를 삭제합니다.
     *
     * @param username 로그인한 사용자의 이메일
     * @param accountId 삭제할 계좌의 ID
     * @throws MemberNotFoundException 사용자를 찾을 수 없는 경우
     * @throws AccountNotFoundException 계좌를 찾을 수 없는 경우
     */
    void deleteAccount(String username, Long accountId);
}
