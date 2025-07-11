package com.ureca.snac.wallet.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.WALLET_NOT_FOUND;

public class WalletNotFoundException extends BusinessException {
    public WalletNotFoundException() {
        super(WALLET_NOT_FOUND);
    }
}