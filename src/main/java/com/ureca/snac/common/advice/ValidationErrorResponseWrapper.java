package com.ureca.snac.common.advice;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(staticName = "of")
public class ValidationErrorResponseWrapper {
    private List<ValidationErrorResponse> fieldErrors;
    private List<ValidationErrorResponse> globalErrors;
}
