package no.acntech.rule;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;

import no.acntech.model.Action;
import no.acntech.model.Resource;

public interface CrudPermissionRule extends PermissionRule {

    default boolean hasPermission(@NonNull final Action action,
                                  @NonNull final Resource resource,
                                  @NonNull final Authentication authentication,
                                  @NonNull final Object[] arguments) {
        return switch (action) {
            case READ -> hasReadPermission(action, resource, authentication, arguments);
            case CREATE -> hasCreatePermission(action, resource, authentication, arguments);
            case UPDATE -> hasUpdatePermission(action, resource, authentication, arguments);
            case DELETE -> hasDeletePermission(action, resource, authentication, arguments);
        };
    }

    boolean hasReadPermission(@NonNull final Action action,
                              @NonNull final Resource resource,
                              @NonNull final Authentication authentication,
                              @NonNull final Object[] arguments);

    boolean hasCreatePermission(@NonNull final Action action,
                                @NonNull final Resource resource,
                                @NonNull final Authentication authentication,
                                @NonNull final Object[] arguments);

    boolean hasUpdatePermission(@NonNull final Action action,
                                @NonNull final Resource resource,
                                @NonNull final Authentication authentication,
                                @NonNull final Object[] arguments);

    boolean hasDeletePermission(@NonNull final Action action,
                                @NonNull final Resource resource,
                                @NonNull final Authentication authentication,
                                @NonNull final Object[] arguments);
}
