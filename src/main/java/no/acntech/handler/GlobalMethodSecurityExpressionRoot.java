package no.acntech.handler;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import java.util.List;

import no.acntech.annotation.Permission;
import no.acntech.rule.PermissionRule;

public class GlobalMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private final MethodInvocation methodInvocation;
    private final List<PermissionRule> permissionRules;
    private Object filterObject;
    private Object returnObject;

    public GlobalMethodSecurityExpressionRoot(final Authentication authentication,
                                              final MethodInvocation methodInvocation,
                                              final List<PermissionRule> permissionRules) {
        super(authentication);
        this.methodInvocation = methodInvocation;
        this.permissionRules = permissionRules;
    }

    public boolean hasPermission() {
        final var accessAnnotation = methodInvocation.getMethod().getAnnotation(Permission.class);
        if (accessAnnotation != null) {
            final var action = accessAnnotation.action();
            final var resource = accessAnnotation.resource();
            final var relevantPermissionRules = permissionRules.stream()
                    .filter(permissionRule -> permissionRule.isRelevant(action, resource))
                    .toList();
            return !relevantPermissionRules.isEmpty() && relevantPermissionRules.stream()
                    .allMatch(permissionRule -> permissionRule.hasPermission(action, resource, authentication, methodInvocation));
        }
        return false;
    }

    @Override
    public void setFilterObject(final Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(final Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }
}
