package no.acntech.converter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import no.acntech.model.UserRole;

public final class RoleConverter {

    public static final String AUTHORITY_ROLE_PREFIX = "ROLE_";

    private RoleConverter() {
    }

    public static List<UserRole> getRoles(final Collection<? extends GrantedAuthority> authorities) {
        if (authorities == null) {
            return Collections.emptyList();
        } else {
            return authorities.stream()
                    .map(RoleConverter::getRole)
                    .toList();
        }
    }

    public static List<GrantedAuthority> getAuthorities(final UserRole... roles) {
        if (roles == null) {
            return Collections.emptyList();
        } else {
            return Arrays.stream(roles)
                    .map(RoleConverter::getAuthority)
                    .toList();
        }
    }

    public static List<GrantedAuthority> getAuthorities(final Collection<UserRole> roles) {
        if (roles == null) {
            return Collections.emptyList();
        } else {
            return roles.stream()
                    .map(RoleConverter::getAuthority)
                    .toList();
        }
    }

    public static UserRole getRole(final GrantedAuthority authority) {
        final var role = authority.getAuthority().replace(AUTHORITY_ROLE_PREFIX, "");
        return UserRole.valueOf(role);
    }

    public static GrantedAuthority getAuthority(final UserRole role) {
        return new SimpleGrantedAuthority(AUTHORITY_ROLE_PREFIX.concat(role.name()));
    }
}
