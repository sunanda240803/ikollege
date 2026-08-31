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
import java.util.Locale;

import static com.iitm.hosteldine.validator.fieldValidators.ValidStringField.*;

@Constraint(validatedBy = TextFieldValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStringField {

	static final String INVALID = "Invalid";
	static final String FIELD = "Field";

	String message() default INVALID;

	String fieldName() default FIELD;

	int min() default 0;

	int max() default 0;

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}

class TextFieldValidator implements ConstraintValidator<ValidStringField, String> {

	private int min;
	private int max;
	private String fieldName;
	private String defaultMessage;
	private final MessageSource messageSource;

	private static final String minMessage = "%s must be at least %d characters";
	private static final String maxMessage = "%s must not exceed %d characters";

    TextFieldValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
	public void initialize(ValidStringField constraintAnnotation) {
		this.min = constraintAnnotation.min();
		this.max = constraintAnnotation.max();
		this.fieldName = messageSource.getMessage(constraintAnnotation.fieldName(), null, Locale.getDefault());

		if(!constraintAnnotation.message().equals(INVALID)){
			this.defaultMessage = messageSource.getMessage(constraintAnnotation.message(), null, Locale.getDefault());
		}
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.trim().isEmpty()) {
			return addViolation(context, defaultMessage);
		}

		int length = value.length();
		if ((min > 0 || max > 0) && !fieldName.equalsIgnoreCase(FIELD) && (length < min || length > max)){
				String sizeMessage = length < min
						? String.format(minMessage, fieldName, min)
						: String.format(maxMessage, fieldName, max);
				return addViolation(context, sizeMessage);
		}
		return true;
	}

	private boolean addViolation(ConstraintValidatorContext context, String message) {
		context.disableDefaultConstraintViolation();
		context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
		return false;
	}
}