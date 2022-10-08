package no.acntech.rule;

import org.springframework.lang.NonNull;

import no.acntech.model.MemberRole;
import no.acntech.model.UserRole;
import no.acntech.service.MembershipService;
import no.acntech.service.SecurityService;

public abstract class MembershipAwarePermissionRule implements CrudPermissionRule {

    private final SecurityService securityService;
    private final MembershipService membershipService;

    protected MembershipAwarePermissionRule(final SecurityService securityService,
                                            final MembershipService membershipService) {
        this.securityService = securityService;
        this.membershipService = membershipService;
    }

    protected boolean isSelf(@NonNull final String username) {
        return username.equals(securityService.getUsername());
    }

    protected boolean isAdmin() {
        return securityService.hasRole(UserRole.ADMIN);
    }

    protected boolean isUser() {
        return securityService.hasRole(UserRole.USER);
    }

    protected boolean isAnonymous() {
        return securityService.isAnonymous();
    }

    protected boolean isMember(@NonNull final String name) {
        final var username = securityService.getUsername();
        return membershipService.hasAnyRoles(name, username, MemberRole.MEMBER);
    }

    protected boolean isOwner(@NonNull final String name) {
        final var username = securityService.getUsername();
        return membershipService.hasAnyRoles(name, username, MemberRole.OWNER);
    }

    protected boolean isMemberOrOwner(@NonNull final String name) {
        final var username = securityService.getUsername();
        return membershipService.hasAnyRoles(name, username, MemberRole.MEMBER, MemberRole.OWNER);
    }
}
