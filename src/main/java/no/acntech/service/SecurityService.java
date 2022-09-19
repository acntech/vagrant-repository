package no.acntech.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Validated
@Service
public class SecurityService {

    public static final String AUTHORITY_ROLE_PREFIX = "ROLE_";

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

    public List<String> getRoles() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof final UsernamePasswordAuthenticationToken authenticationToken) {
            final var authorities = authenticationToken.getAuthorities();
            return getRoles(authorities);
        } else {
            throw new AuthenticationCredentialsNotFoundException("Security context is empty or holds authentication object of illegal type");
        }
    }

    public List<String> getRoles(final Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null) {
            return Collections.emptyList();
        } else {
            return authorities.stream()
                    .map(this::getRole)
                    .toList();
        }
    }

    public List<GrantedAuthority> getAuthorities(final String... roles) {
        if (roles == null) {
            return Collections.emptyList();
        } else {
            return Arrays.stream(roles)
                    .map(this::getAuthority)
                    .toList();
        }
    }

    public List<GrantedAuthority> getAuthorities(final Collection<String> roles) {
        if (roles == null) {
            return Collections.emptyList();
        } else {
            return roles.stream()
                    .map(this::getAuthority)
                    .toList();
        }
    }

    public String getRole(GrantedAuthority authority) {
        return authority.getAuthority().replace(AUTHORITY_ROLE_PREFIX, "");
    }

    public GrantedAuthority getAuthority(String role) {
        return new SimpleGrantedAuthority(AUTHORITY_ROLE_PREFIX.concat(role));
    }

    public boolean isAuthenticated() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }
}
