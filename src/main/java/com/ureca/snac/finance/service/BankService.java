package com.ureca.snac.finance.service;

import com.ureca.snac.finance.controller.request.CreateBankRequest;
import com.ureca.snac.finance.controller.request.UpdateBankRequest;
import com.ureca.snac.finance.exception.BankNotFoundException;
import com.ureca.snac.finance.service.response.BankResponse;

import java.util.List;

public interface BankService {

    /**
     * 은행 정보를 등록하고 생성된 은행의 ID를 반환합니다.
     *
     * @param createBankRequest 등록할 은행 정보
     * @return 생성된 은행의 식별자(ID)
     */
    Long createBank(CreateBankRequest createBankRequest);

    /**
     * 주어진 ID에 해당하는 은행 정보를 조회합니다.
     *
     * @param bankId 조회할 은행 ID
     * @return 조회된 은행 정보
     * @throws BankNotFoundException 은행을 찾을 수 없는 경우
     */
    BankResponse getBankById(Long bankId);

    /**
     * 전체 은행 목록을 조회합니다.
     *
     * @return 은행 응답 객체 리스트
     */
    List<BankResponse> getAllBanks();

    /**
     * 주어진 ID에 해당하는 은행의 이름을 수정합니다.
     *
     * @param bankId 수정할 은행 ID
     * @param updateBankRequest 수정할 이름 정보
     * @throws BankNotFoundException 은행을 찾을 수 없는 경우
     */
    void updateBank(Long bankId, UpdateBankRequest updateBankRequest);

    /**
     * 주어진 ID에 해당하는 은행 정보를 삭제합니다.
     *
     * @param bankId 삭제할 은행 ID
     */
    void deleteBank(Long bankId);
}
