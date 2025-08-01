package com.ureca.snac.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NicknameValidator implements ConstraintValidator<NicknameConstraint, String> {

    // 첫 글자는 영어 또는 한글 으로만  !
    // 이후: 영어, 한글, 숫자, 지정 특수문자
    private static final Pattern PATTERN = Pattern.compile(
            "^[A-Za-z\\uAC00-\\uD7A3][A-Za-z0-9\\uAC00-\\uD7A3!?@#$%^&*()~`+\\-_]{1,9}$"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return PATTERN.matcher(value).matches();
    }
}
