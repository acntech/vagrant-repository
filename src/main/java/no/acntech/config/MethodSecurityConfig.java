package no.acntech.config;

import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import java.util.List;

import no.acntech.handler.GlobalMethodSecurityExpressionHandler;
import no.acntech.rule.PermissionRule;

@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final List<PermissionRule> permissionRules;

    public MethodSecurityConfig(final List<PermissionRule> permissionRules) {
        this.permissionRules = permissionRules;
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new GlobalMethodSecurityExpressionHandler(permissionRules);
    }
}
