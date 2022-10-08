package no.acntech.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import no.acntech.converter.RoleConverter;
import no.acntech.model.SecurityUser;
import no.acntech.model.UserRole;

@Validated
@Service
public class SecurityService {


    private final String systemUsername;

    public SecurityService(@Value("${acntech.security.system-user.username}") final String systemUsername) {
        this.systemUsername = systemUsername;
    }

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

    public List<UserRole> getRoles() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof final UsernamePasswordAuthenticationToken authenticationToken) {
            final var authorities = authenticationToken.getAuthorities();
            return RoleConverter.getRoles(authorities);
        } else {
            throw new AuthenticationCredentialsNotFoundException("Security context is empty or holds authentication object of illegal type");
        }
    }


    public boolean hasRole(final UserRole role) {
        final var roles = getRoles();
        return roles.contains(role);
    }

    public boolean hasAnyRole(final UserRole... roles) {
        return roles.length > 0 && Arrays.stream(roles)
                .anyMatch(this::hasRole);
    }

    public boolean isAuthenticated() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }

    public boolean isAnonymous() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(authentication -> authentication instanceof AnonymousAuthenticationToken)
                .orElse(false);
    }

    public void createSystemSession() {
        if (isAuthenticated()) {
            throw new SessionAuthenticationException("Session already set");
        }
        final var authorities = RoleConverter.getAuthorities(UserRole.ADMIN);
        final var principal = SecurityUser.builder()
                .username(systemUsername)
                .authorities(authorities)
                .build();
        final var authentication = UsernamePasswordAuthenticationToken.authenticated(principal, null, authorities);
        final var securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    public void clearSystemSession() {
        if (isAuthenticated()) {
            final var username = getUsername();
            if (systemUsername.equals(username)) {
                final var securityContext = SecurityContextHolder.createEmptyContext();
                SecurityContextHolder.setContext(securityContext);
            } else {
                throw new SessionAuthenticationException("Session is not a system session");
            }
        }
    }
}
