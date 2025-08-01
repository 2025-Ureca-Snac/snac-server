package com.ureca.snac.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<PasswordConstraint, String> {

    // 영어 1개 이상, 숫자 1개 이상, 지정 특수문자 1개 이상, 길이 6~12
    private static final Pattern PATTERN = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!?@#$%^&*()~`+\\-_])[A-Za-z\\d!?@#$%^&*()~`+\\-_]{6,12}$"
    );

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return PATTERN.matcher(value).matches();
    }
}
