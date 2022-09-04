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
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationConverter;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import no.acntech.service.TokenService;

import static no.acntech.service.TokenService.ROLE_CLAIM_NAME;

public class BearerTokenAuthenticationConverter implements AuthenticationConverter {

    public static final String BEARER_TOKEN_PREFIX = "Bearer";
    public static final String AUTHORITY_ROLE_PREFIX = "ROLE_";
    private final TokenService tokenService;

    public BearerTokenAuthenticationConverter(final TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public UsernamePasswordAuthenticationToken convert(final HttpServletRequest request) {
        final var bearerToken = getBearerToken(request);
        final var claims = tokenService.parseToken(bearerToken);
        final var subject = claims.getSubject();
        if (StringUtils.isBlank(subject)) {
            throw new BadCredentialsException("Malformed bearer token");
        }
        final var roles = getRolesClaim(claims);
        final var authorities = roles.stream()
                .map(AUTHORITY_ROLE_PREFIX::concat)
                .map(SimpleGrantedAuthority::new).toList();
        return UsernamePasswordAuthenticationToken.authenticated(subject, null, authorities);
    }

    public List<String> getRolesClaim(final JWTClaimsSet claims) {
        try {
            return claims.getStringListClaim(ROLE_CLAIM_NAME);
        } catch (ParseException e) {
            return Collections.emptyList();
        }
    }

    private String getBearerToken(final HttpServletRequest request) {
        var header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(header)) {
            return null;
        }
        header = header.trim();
        if (!StringUtils.startsWithIgnoreCase(header, BEARER_TOKEN_PREFIX)) {
            return null;
        }
        header = header.replace(BEARER_TOKEN_PREFIX, "").trim();
        if (StringUtils.isBlank(header)) {
            throw new BadCredentialsException("Empty bearer token");
        }
        return header;
    }
}
