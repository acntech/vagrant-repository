package no.acntech.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import no.acntech.converter.TokenAuthenticationConverter;

public class TokenAuthenticationFilter extends GenericFilterBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenAuthenticationFilter.class);
    private final RequestMatcher requiresAuthenticationRequestMatcher;
    private final TokenAuthenticationConverter authenticationConverter;
    private final SecurityContextRepository securityContextRepository;

    public TokenAuthenticationFilter(final RequestMatcher requiresAuthenticationRequestMatcher,
                                     final TokenAuthenticationConverter authenticationConverter,
                                     final SecurityContextRepository securityContextRepository) {
        this.requiresAuthenticationRequestMatcher = requiresAuthenticationRequestMatcher;
        this.authenticationConverter = authenticationConverter;
        this.securityContextRepository = securityContextRepository;
    }

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) throws ServletException, IOException {
        final var request = (HttpServletRequest) servletRequest;
        final var response = (HttpServletResponse) servletResponse;
        if (requiresAuthentication(request)) {
            final var authenticationToken = authenticationConverter.convert(request);
            final var securityContext = SecurityContextHolder.createEmptyContext();
            if (authenticationToken != null) {
                securityContext.setAuthentication(authenticationToken);
                SecurityContextHolder.setContext(securityContext);
            }
            securityContextRepository.saveContext(securityContext, request, response);
        }
        filterChain.doFilter(request, response);
    }

    private boolean requiresAuthentication(final HttpServletRequest request) {
        return requiresAuthenticationRequestMatcher.matches(request) && !isAuthenticated();
    }

    private boolean isAuthenticated() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }
}
