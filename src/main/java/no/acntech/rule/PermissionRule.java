package no.acntech.rule;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;

import no.acntech.model.Action;
import no.acntech.model.Resource;

public interface PermissionRule {

    boolean isRelevant(@NonNull final Action action,
                       @NonNull final Resource resource);

    boolean hasPermission(@NonNull final Action action,
                          @NonNull final Resource resource,
                          @NonNull final Authentication authentication,
                          @NonNull final Object[] arguments);

    default boolean hasPermission(@NonNull final Action action,
                                  @NonNull final Resource resource,
                                  @NonNull final Authentication authentication,
                                  @NonNull final MethodInvocation methodInvocation) {
        return hasPermission(action, resource, authentication, methodInvocation.getArguments());
    }
}
