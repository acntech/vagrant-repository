package no.acntech.repository;

import com.nimbusds.jwt.JWTClaimsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import no.acntech.service.HttpRequestService;
import no.acntech.service.TokenService;

public class InMemorySecurityContextRepository implements SecurityContextRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(InMemorySecurityContextRepository.class);
    private final Map<String, SecurityContext> securityContextStore = new ConcurrentHashMap<>();
    private final HttpRequestService httpRequestService;
    private final TokenService tokenService;

    public InMemorySecurityContextRepository(final HttpRequestService httpRequestService,
                                             final TokenService tokenService) {
        this.httpRequestService = httpRequestService;
        this.tokenService = tokenService;
    }

    @Override
    public SecurityContext loadContext(final HttpRequestResponseHolder requestResponseHolder) {
        LOGGER.trace("Loading security context");
        return getTokenSubject(requestResponseHolder.getRequest())
                .map(securityContextStore::get)
                .orElseGet(SecurityContextHolder::createEmptyContext);
    }

    @Override
    public void saveContext(final SecurityContext securityContext,
                            final HttpServletRequest request,
                            final HttpServletResponse response) {
        LOGGER.trace("Save security context");
        Optional.ofNullable(securityContext)
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .ifPresent(username -> securityContextStore.put(username, securityContext));
    }

    @Override
    public boolean containsContext(final HttpServletRequest request) {
        LOGGER.trace("Contains security context");
        return getTokenSubject(request)
                .map(securityContextStore::containsKey)
                .orElse(false);
    }

    private Optional<String> getTokenSubject(final HttpServletRequest request) {
        return Optional.ofNullable(request)
                .map(this::getToken)
                .map(tokenService::parseToken)
                .map(JWTClaimsSet::getSubject);
    }

    private String getToken(final HttpServletRequest request) {
        return Optional.ofNullable(request)
                .map(httpRequestService::readHeader)
                .orElseGet(() -> httpRequestService.readCookie(request));
    }
}
