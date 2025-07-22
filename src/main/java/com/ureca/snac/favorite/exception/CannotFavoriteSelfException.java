package com.ureca.snac.favorite.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.CANNOT_FAVORITE_SELF;

public class CannotFavoriteSelfException extends BusinessException {
    public CannotFavoriteSelfException() {
        super(CANNOT_FAVORITE_SELF);
    }
}
