package com.ureca.snac.money.exception;

import com.ureca.snac.common.exception.BusinessException;

import static com.ureca.snac.common.BaseCode.ORDER_NOT_FOUND;

public class OrderNotFoundException extends BusinessException {
    public OrderNotFoundException() {
        super(ORDER_NOT_FOUND);
    }
}
