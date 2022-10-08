package no.acntech.validation;

import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValuesMatchValidator implements ConstraintValidator<ValuesMatch, Object> {

    private String message;
    private String field;
    private String fieldMatch;

    @Override
    public void initialize(final ValuesMatch annotation) {
        this.message = annotation.message();
        this.field = annotation.field();
        this.fieldMatch = annotation.fieldMatch();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        final var fieldValue = new BeanWrapperImpl(value).getPropertyValue(field);
        final var fieldMatchValue = new BeanWrapperImpl(value).getPropertyValue(fieldMatch);

        if (fieldValue == null) {
            if (fieldMatchValue == null) {
                return true;
            } else {
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(field)
                        .addConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(fieldMatch)
                        .addConstraintViolation();
                return false;
            }
        } else {
            if (fieldValue.equals(fieldMatchValue)) {
                return true;
            } else {
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(field)
                        .addConstraintViolation();
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(fieldMatch)
                        .addConstraintViolation();
                return false;
            }
        }
    }
}
