package com.ureca.snac.asset.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.INVALID_ASSET_AMOUNT;

public class InvalidAssetAmountException extends BusinessException {
    public InvalidAssetAmountException() {
        super(INVALID_ASSET_AMOUNT);
    }
}
