package no.acntech.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import no.acntech.converter.TokenAuthenticationConverter;
import no.acntech.filter.TokenAuthenticationFilter;

public class TokenSecurityConfig extends AbstractHttpConfigurer<TokenSecurityConfig, HttpSecurity> {

    private TokenAuthenticationConverter tokenAuthenticationConverter;
    private RequestMatcher requiresAuthenticationRequestMatcher;

    @Override
    public void configure(final HttpSecurity http) {
        final var securityContextRepository = http.getSharedObject(SecurityContextRepository.class);
        final var tokenAuthenticationFilter = new TokenAuthenticationFilter(
                requiresAuthenticationRequestMatcher,
                tokenAuthenticationConverter,
                securityContextRepository);
        http.addFilterAfter(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    public TokenSecurityConfig tokenAuthenticationConverter(final TokenAuthenticationConverter tokenAuthenticationConverter) {
        Assert.notNull(tokenAuthenticationConverter, "TokenAuthenticationConverter is null");
        this.tokenAuthenticationConverter = tokenAuthenticationConverter;
        return this;
    }

    public TokenSecurityConfig requiresAuthentication(final RequestMatcher requiresAuthenticationRequestMatcher) {
        Assert.notNull(requiresAuthenticationRequestMatcher, "RequestMatcher is null");
        this.requiresAuthenticationRequestMatcher = requiresAuthenticationRequestMatcher;
        return this;
    }

    public static TokenSecurityConfig tokenSecurity() {
        return new TokenSecurityConfig();
    }
}
