package com.ureca.snac.common.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationErrorResponse {

    private String field;
    private String rejectedValue;
    private String code;
    private String message;
}
