package no.acntech.rule;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import no.acntech.model.Action;
import no.acntech.model.Resource;
import no.acntech.service.MembershipService;
import no.acntech.service.SecurityService;

@Component
public class UploadPermissionRule extends MembershipAwarePermissionRule {


    protected UploadPermissionRule(final SecurityService securityService,
                                   final MembershipService membershipService) {
        super(securityService, membershipService);
    }

    @Override
    public boolean isRelevant(@NonNull final Action action,
                              @NonNull final Resource resource) {
        return resource == Resource.UPLOADS;
    }

    @Override
    public boolean hasReadPermission(@NonNull final Action action,
                                     @NonNull final Resource resource,
                                     @NonNull final Authentication authentication,
                                     @NonNull final Object[] arguments) {
        return isAdmin() || isUser();
    }

    @Override
    public boolean hasCreatePermission(@NonNull final Action action,
                                       @NonNull final Resource resource,
                                       @NonNull final Authentication authentication,
                                       @NonNull final Object[] arguments) {
        if (arguments.length > 0 && arguments[0] instanceof final String username) {
            return isAdmin() || isMemberOrOwner(username);
        } else {
            return false;
        }
    }

    @Override
    public boolean hasUpdatePermission(@NonNull final Action action,
                                       @NonNull final Resource resource,
                                       @NonNull final Authentication authentication,
                                       @NonNull final Object[] arguments) {
        if (arguments.length > 0 && arguments[0] instanceof final String username) {
            return isAdmin() || isMemberOrOwner(username);
        } else {
            return false;
        }
    }

    @Override
    public boolean hasDeletePermission(@NonNull final Action action,
                                       @NonNull final Resource resource,
                                       @NonNull final Authentication authentication,
                                       @NonNull final Object[] arguments) {
        if (arguments.length > 0 && arguments[0] instanceof final String username) {
            return isAdmin() || isMemberOrOwner(username);
        } else {
            return false;
        }
    }
}
