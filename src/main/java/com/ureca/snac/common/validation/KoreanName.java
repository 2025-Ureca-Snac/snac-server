package com.ureca.snac.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.*;
import java.util.regex.Pattern;

@Documented
@Constraint(validatedBy = KoreanName.Validator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface KoreanName {
    String message() default "이름은 한글 2~5자여야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<KoreanName, String> {
        private static final Pattern PATTERN = Pattern.compile("^[가-힣]{2,5}$");

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) return true;
            return PATTERN.matcher(value).matches();
        }
    }
}
