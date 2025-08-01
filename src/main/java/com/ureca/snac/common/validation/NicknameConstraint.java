package com.ureca.snac.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NicknameValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NicknameConstraint {
    String message() default "닉네임은 2~10자여야 하며, 영어/한글로 시작하고 이후에는 영어, 한글, 숫자, 특수기호 ! ? @ # $ % ^ & * ( ) ~ ` + - _ 만 사용할 수 있습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
