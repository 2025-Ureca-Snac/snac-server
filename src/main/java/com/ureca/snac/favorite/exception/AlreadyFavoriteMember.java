package com.ureca.snac.favorite.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.ALREADY_FAVORITE_MEMBER;

public class AlreadyFavoriteMember extends BusinessException {
    public AlreadyFavoriteMember() {
        super(ALREADY_FAVORITE_MEMBER);
    }
}
