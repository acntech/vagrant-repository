package no.acntech.validation;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class CorrectVersionValidator implements ConstraintValidator<CorrectVersion, Object> {

    private static final String REGEX = "^(0|([1-9][0-9]*))(?:\\.(0|([1-9][0-9]*))(?:\\.(0|([1-9][0-9]*)))?)?$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    @Override
    public void initialize(final CorrectVersion annotation) {
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return true;
        } else {
            return PATTERN.matcher(value.toString()).matches();
        }
    }
}
