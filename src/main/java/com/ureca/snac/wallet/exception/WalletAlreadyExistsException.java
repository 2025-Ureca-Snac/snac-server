package com.ureca.snac.wallet.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.WALLET_ALREADY_EXISTS;

public class WalletAlreadyExistsException extends BusinessException {
    public WalletAlreadyExistsException() {
        super(WALLET_ALREADY_EXISTS);
    }
}