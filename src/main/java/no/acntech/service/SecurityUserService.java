package no.acntech.service;

import org.jooq.exception.NoDataFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import no.acntech.model.SecurityUser;

public class SecurityUserService implements UserDetailsService {

    private final UserService userService;
    private final SecurityService securityService;

    public SecurityUserService(final UserService userService,
                               final SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        Assert.hasText(username, "Username is blank");
        try {
            final var users = userService.getUser(username);
            final var authorities = securityService.getAuthorities(users.role().name());
            return SecurityUser.builder()
                    .username(users.username())
                    .password(users.passwordHash())
                    .authorities(authorities)
                    .build();
        } catch (NoDataFoundException e) {
            throw new UsernameNotFoundException("Unknown user", e);
        }
    }
}
