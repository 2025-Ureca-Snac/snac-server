package com.ureca.snac.board.exception;

import com.ureca.snac.common.BaseCode;
import com.ureca.snac.common.exception.BusinessException;

public class ArticleNotFoundException extends BusinessException {
    public ArticleNotFoundException() {
        super(BaseCode.ARTICLE_NOT_FOUND);
    }
}
