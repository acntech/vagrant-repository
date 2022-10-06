package no.acntech.handler;

import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

import java.util.List;

import no.acntech.rule.PermissionRule;

public class GlobalMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalMethodSecurityExpressionHandler.class);
    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private final List<PermissionRule> permissionRules;

    public GlobalMethodSecurityExpressionHandler(final List<PermissionRule> permissionRules) {
        this.permissionRules = permissionRules;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(final Authentication authentication,
                                                                              final MethodInvocation methodInvocation) {
        final var expressionRoot = new GlobalMethodSecurityExpressionRoot(authentication, methodInvocation, permissionRules);
        expressionRoot.setPermissionEvaluator(getPermissionEvaluator());
        expressionRoot.setTrustResolver(trustResolver);
        expressionRoot.setRoleHierarchy(getRoleHierarchy());
        return expressionRoot;
    }
}
