package com.ureca.snac.wallet.dto;

import com.ureca.snac.wallet.entity.Wallet;

public record WalletSummaryResponse(
        Long money,
        Long point
) {
    /**
     * 엔티티 객체를 받아서 DTO를 반환하는 정적 팩토리 메소드
     *
     * @param wallet 반환할 원본 엔티티
     * @return DTO
     */
    public static WalletSummaryResponse from(Wallet wallet) {
        return new WalletSummaryResponse(wallet.getMoney(), wallet.getPoint());
    }
}
