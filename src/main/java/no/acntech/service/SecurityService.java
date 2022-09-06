package no.acntech.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public String getUsername() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof final UsernamePasswordAuthenticationToken authenticationToken) {
            final var username = authenticationToken.getName();
            if (StringUtils.isBlank(username)) {
                throw new BadCredentialsException("Authentication object holds blank username");
            } else {
                return username;
            }
        } else {
            throw new AuthenticationCredentialsNotFoundException("Security context is empty or holds authentication object of illegal type");
        }
    }
}
