package com.ureca.snac.common.validation;

import com.ureca.snac.member.dto.request.PasswordChangeRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DifferentPasswordsValidator implements ConstraintValidator<DifferentPasswords, PasswordChangeRequest> {

    @Override
    public boolean isValid(PasswordChangeRequest value, ConstraintValidatorContext context) {
        if (value == null) return true;
        String current = value.getCurrentPwd();
        String neu = value.getNewPwd();
        if (current == null || neu == null) return true;
        if (current.equals(neu)) {
            return false;
        }
        return true;
    }
}
