package no.acntech.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import no.acntech.repository.UserRepository;
import no.acntech.service.SecurityService;

public class CorrectPasswordValidator implements ConstraintValidator<CorrectPassword, Object> {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SecurityService securityService;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(final CorrectPassword annotation) {
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return true;
        } else {
            final var username = securityService.getUsername();
            final var usersRecord = userRepository.getUser(username);
            final var passwordHash = usersRecord.getPasswordHash();
            return passwordEncoder.matches(value.toString(), passwordHash);
        }
    }
}
