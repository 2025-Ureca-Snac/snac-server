package com.ureca.snac.asset.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.INVALID_ASSET_BALANCE;

public class InvalidAssetBalanceException extends BusinessException {
    public InvalidAssetBalanceException() {
        super(INVALID_ASSET_BALANCE);
    }
}
