package com.ureca.snac.wallet.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.*;

public class WalletAlreadyExistsException extends BusinessException {

    public WalletAlreadyExistsException() {
        super(WALLET_ALREADY_EXISTS);
    }
}