package com.ureca.snac.auth.exception.lmm;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class ImageValidationBusinessException extends BusinessException {

    public ImageValidationBusinessException(BaseCode baseCode, String msg) {
        super(baseCode, msg);
    }
}
