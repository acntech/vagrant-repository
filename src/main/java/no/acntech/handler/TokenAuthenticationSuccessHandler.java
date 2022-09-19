package no.acntech.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import no.acntech.service.HttpRequestService;
import no.acntech.service.SecurityService;
import no.acntech.service.TokenService;

public class TokenAuthenticationSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements AuthenticationSuccessHandler {

    private final RequestMatcher redirectForRequestMatcher;
    private final String redirectUrl;
    private final HttpRequestService httpRequestService;
    private final SecurityService securityService;
    private final TokenService tokenService;

    public TokenAuthenticationSuccessHandler(final RequestMatcher redirectForRequestMatcher,
                                             final String redirectUrl,
                                             final HttpRequestService httpRequestService,
                                             final SecurityService securityService,
                                             final TokenService tokenService) {
        this.redirectForRequestMatcher = redirectForRequestMatcher;
        this.redirectUrl = redirectUrl;
        this.httpRequestService = httpRequestService;
        this.securityService = securityService;
        this.tokenService = tokenService;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Authentication authentication) throws IOException {
        setTokenCookie(response, authentication);
        if (redirectForRequestMatcher.matches(request)) {
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        }
    }

    private void setTokenCookie(final HttpServletResponse response,
                                final Authentication authentication) {
        final var username = authentication.getName();
        final var roles = securityService.getRoles(authentication.getAuthorities());
        final var signedJWT = tokenService.createToken(username, roles);
        final var token = signedJWT.serialize();
        httpRequestService.addCookie(response, token);
    }
}
