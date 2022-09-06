package no.acntech.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import no.acntech.exception.ItemNotFoundException;
import no.acntech.model.Login;
import no.acntech.model.Password;
import no.acntech.model.Token;
import no.acntech.model.User;

import static no.acntech.converter.BearerTokenAuthenticationConverter.BEARER_TOKEN_PREFIX;

@Service
public class AuthenticateService {

    private final PasswordService passwordService;
    private final TokenService tokenService;
    private final UserService userService;
    private final Map<String, Token> TOKEN_STORE = new HashMap<>();

    public AuthenticateService(final PasswordService passwordService,
                               final TokenService tokenService,
                               final UserService userService) {
        this.passwordService = passwordService;
        this.tokenService = tokenService;
        this.userService = userService;
    }

    public Token createToken(@Valid @NotNull Login login) {
        final var user = getUser(login.user().login());
        final var password = new Password(user.passwordHash(), user.passwordSalt());
        if (passwordService.verifyPassword(login.user().password(), password)) {
            if (TOKEN_STORE.containsKey(login.user().login())) {
                return TOKEN_STORE.get(login.user().login());
            } else {
                final var signedJWT = tokenService.createToken(user.username(), Collections.singletonList(user.role().name()));
                final var jwt = signedJWT.serialize();
                final var createdAt = tokenService.getIssuedDateTime(signedJWT);
                final var token = new Token(login.token().description(), jwt, "", createdAt);
                TOKEN_STORE.put(login.user().login(), token);
                return token;
            }
        } else {
            throw new BadCredentialsException("Incorrect username and password combination");
        }
    }

    public boolean verifyTokenHeader(String header) {
        if (StringUtils.isBlank(header)) {
            return false;
        }
        if (!StringUtils.startsWithIgnoreCase(header, BEARER_TOKEN_PREFIX)) {
            return false;
        }
        final var token = header.replace(BEARER_TOKEN_PREFIX, "").trim();
        return tokenService.verifyToken(token);
    }

    public void deleteToken(String header) {
        if (StringUtils.isBlank(header)) {
            throw new BadCredentialsException("Bearer token not set");
        }
        if (!StringUtils.startsWithIgnoreCase(header, BEARER_TOKEN_PREFIX)) {
            throw new BadCredentialsException("Bearer token has bad format");
        }
        final var token = header.replace(BEARER_TOKEN_PREFIX, "").trim();
        final var jwtClaimsSet = tokenService.parseToken(token);
        TOKEN_STORE.remove(jwtClaimsSet.getSubject());
    }

    public User getUser(@NotBlank String username) {
        try {
            return userService.getUser(username);
        } catch (ItemNotFoundException e) {
            throw new BadCredentialsException("No user found for username " + username, e);
        }
    }
}
