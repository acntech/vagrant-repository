/*
 * Copyright 2002-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.acntech.converter;

import com.nimbusds.jwt.JWTClaimsSet;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import no.acntech.service.HttpRequestService;
import no.acntech.service.SecurityService;
import no.acntech.service.TokenService;

import static no.acntech.service.TokenService.ROLE_CLAIM_NAME;

public class TokenAuthenticationConverter implements AuthenticationConverter {

    private final RequestMatcher requiresAuthenticationRequestMatcher;
    private final HttpRequestService httpRequestService;
    private final SecurityService securityService;
    private final TokenService tokenService;

    public TokenAuthenticationConverter(final RequestMatcher requiresAuthenticationRequestMatcher,
                                        final HttpRequestService httpRequestService,
                                        final SecurityService securityService,
                                        final TokenService tokenService) {
        this.requiresAuthenticationRequestMatcher = requiresAuthenticationRequestMatcher;
        this.httpRequestService = httpRequestService;
        this.securityService = securityService;
        this.tokenService = tokenService;
    }

    @Override
    public UsernamePasswordAuthenticationToken convert(final HttpServletRequest request) {
        final var token = getToken(request);
        if (StringUtils.isBlank(token)) {
            if (requiresAuthenticationRequestMatcher.matches(request)) {
                throw new BadCredentialsException("Missing bearer token");
            } else {
                return null;
            }
        }
        final var claims = tokenService.parseToken(token);
        final var subject = claims.getSubject();
        if (StringUtils.isBlank(subject)) {
            if (requiresAuthenticationRequestMatcher.matches(request)) {
                throw new BadCredentialsException("Malformed bearer token");
            } else {
                return null;
            }
        }
        final var roles = getRolesClaim(claims);
        final var authorities = securityService.getAuthorities(roles);
        return UsernamePasswordAuthenticationToken.authenticated(subject, null, authorities);
    }

    private String getToken(final HttpServletRequest request) {
        return Optional.ofNullable(request)
                .map(httpRequestService::readHeader)
                .orElseGet(() -> httpRequestService.readCookie(request));
    }

    private List<String> getRolesClaim(final JWTClaimsSet claims) {
        try {
            return claims.getStringListClaim(ROLE_CLAIM_NAME);
        } catch (ParseException e) {
            return Collections.emptyList();
        }
    }
}
