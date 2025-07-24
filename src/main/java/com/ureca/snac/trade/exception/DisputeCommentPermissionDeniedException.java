package com.ureca.snac.trade.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class DisputeCommentPermissionDeniedException extends BusinessException {
    public DisputeCommentPermissionDeniedException() {
        super(BaseCode.DISPUTE_COMMENT_PERMISSION_DENIED);
    }
}