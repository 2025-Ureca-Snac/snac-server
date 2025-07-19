package com.ureca.snac.asset.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.INVALID_ASSET_TITLE;

public class InvalidAssetTitleException extends BusinessException {
    public InvalidAssetTitleException() {
        super(INVALID_ASSET_TITLE);
    }
}
