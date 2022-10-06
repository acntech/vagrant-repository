package no.acntech.validation;

import org.apache.commons.lang3.StringUtils;
import org.jooq.exception.NoDataFoundException;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import no.acntech.repository.UserRepository;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, Object> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(final UniqueUsername annotation) {
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return true;
        } else {
            try {
                userRepository.getUser(value.toString());
                return false;
            } catch (NoDataFoundException e) {
                return true;
            }
        }
    }
}
