package com.iitm.hosteldine.validator.fieldValidators;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.springframework.context.MessageSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.util.Locale;

@Constraint(validatedBy = RequiredDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateField {

    String message() default "Date is required";

    String fieldName() default "Date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

class RequiredDateValidator implements ConstraintValidator<ValidDateField, LocalDate> {

    private String fieldName;
    private String defaultMessage;
    private final MessageSource messageSource;

    RequiredDateValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void initialize(ValidDateField constraintAnnotation) {
        this.fieldName = messageSource.getMessage(constraintAnnotation.fieldName(), null, Locale.getDefault());
        this.defaultMessage = messageSource.getMessage(constraintAnnotation.message(), null, Locale.getDefault());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return addViolation(context, String.format("%s is required", fieldName));
        }
        return true;
    }

    private boolean addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        return false;
    }
}
