package com.ureca.snac.asset.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.INVALID_ASSET_SOURCE;
import static com.ureca.snac.common.BaseCode.INVALID_ASSET_TITLE;

public class InvalidAssetSourceException extends BusinessException {
    public InvalidAssetSourceException() {
        super(INVALID_ASSET_SOURCE);
    }
}
