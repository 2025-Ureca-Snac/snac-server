package com.ureca.snac.asset.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.*;

public class AssetHistoryNotFoundException extends BusinessException {
    public AssetHistoryNotFoundException() {
        super(ASSET_HISTORY_NOT_FOUND);
    }
}
