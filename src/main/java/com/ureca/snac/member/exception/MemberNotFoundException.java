package com.ureca.snac.member.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.MEMBER_NOT_FOUND;

public class MemberNotFoundException extends BusinessException {
    public MemberNotFoundException() {
        super(MEMBER_NOT_FOUND);
    }
}
