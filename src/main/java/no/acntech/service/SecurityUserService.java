package no.acntech.service;

import org.jooq.exception.NoDataFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;

import no.acntech.converter.RoleConverter;
import no.acntech.model.SecurityUser;
import no.acntech.model.UserRole;
import no.acntech.repository.UserRepository;

@Validated
@Service
public class SecurityUserService implements UserDetailsService {

    private final UserRepository userRepository;

    public SecurityUserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Assert.hasText(username, "Username is blank");
        try {
            final var record = userRepository.getUser(username);
            final var authority = RoleConverter.getAuthority(UserRole.valueOf(record.getRole()));
            return SecurityUser.builder()
                    .username(record.getUsername())
                    .password(record.getPasswordHash())
                    .authorities(Collections.singleton(authority))
                    .build();
        } catch (NoDataFoundException e) {
            throw new UsernameNotFoundException("Unknown user", e);
        }
    }
}
