package com.example.taa.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validates that a string is a syntactically valid IBAN (structure + ISO 7064
 * mod-97 check digits). Blank values are considered valid so this can be
 * combined with {@code @NotBlank} for a precise, separate "required" message.
 */
@Documented
@Constraint(validatedBy = IbanValidator.class)
@Target({FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface Iban {

	String message() default "{ark.iban.invalid}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
