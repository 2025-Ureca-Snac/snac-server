package com.ureca.snac.favorite.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.FAVORITE_RELATION_NOT_FOUND;

public class FavoriteRelationNotFoundException extends BusinessException {
    public FavoriteRelationNotFoundException() {
        super(FAVORITE_RELATION_NOT_FOUND);
    }
}
