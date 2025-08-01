package com.ureca.snac.common.validation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.io.IOException;
import java.lang.annotation.*;
import java.util.regex.Pattern;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = KoreanPhone.Validator.class)
@JacksonAnnotationsInside
@JsonDeserialize(using = KoreanPhone.Deserializer.class)
public @interface KoreanPhone {
    String message() default "유효한 한국 휴대폰 번호여야 합니다. 예: 01012345678 또는 +821012345678";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<KoreanPhone, String> {
        private static final Pattern PATTERN = Pattern.compile("^(?:010\\d{8}|\\+8210\\d{8})$");

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) return true;
            return PATTERN.matcher(value).matches();
        }
    }

    class Deserializer extends StdDeserializer<String> {
        public Deserializer() {
            super(String.class);
        }

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String raw = p.getValueAsString();
            if (raw == null) return null;

            String cleaned = raw.replaceAll("[\\s.,-]", "");

            if (cleaned.startsWith("+82")) {
                cleaned = "0" + cleaned.substring(3);
            }
            return cleaned;
        }
    }
}
