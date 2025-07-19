package com.ureca.snac.asset.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.INVALID_ASSET_CATEGORY_COMBINATION;

public class InvalidAssetCategoryCombinationException extends BusinessException {
    public InvalidAssetCategoryCombinationException() {
        super(INVALID_ASSET_CATEGORY_COMBINATION);
    }
}
