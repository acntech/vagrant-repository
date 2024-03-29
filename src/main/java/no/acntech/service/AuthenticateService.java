package no.acntech.service;

import org.apache.commons.lang3.StringUtils;
import org.jooq.exception.NoDataFoundException;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;

import no.acntech.model.Login;
import no.acntech.model.Token;
import no.acntech.model.TokenUser;
import no.acntech.model.User;
import no.acntech.repository.TokenRepository;
import no.acntech.repository.UserRepository;

@Validated
@Service
public class AuthenticateService {

    private final ConversionService conversionService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final HttpRequestService httpRequestService;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public AuthenticateService(final ConversionService conversionService,
                               final PasswordEncoder passwordEncoder,
                               final TokenRepository tokenRepository,
                               final HttpRequestService httpRequestService,
                               final TokenService tokenService,
                               final UserRepository userRepository) {
        this.conversionService = conversionService;
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.httpRequestService = httpRequestService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    public Token createToken(@Valid @NotNull final Login login) {
        final var user = getUser(login.user().login());
        if (passwordEncoder.matches(login.user().password(), user.passwordHash())) {
            if (tokenRepository.containsToken(login.user().login())) {
                return tokenRepository.loadToken(login.user().login());
            } else {
                final var signedJWT = tokenService.createToken(user.username(), Collections.singletonList(user.role().name()));
                final var jwt = signedJWT.serialize();
                final var createdAt = tokenService.getIssuedDateTime(signedJWT);
                final var tokenUser = new TokenUser(user.username());
                final var token = new Token(login.token().description(), jwt, null, createdAt, tokenUser);
                tokenRepository.saveToken(login.user().login(), token);
                return token;
            }
        } else {
            throw new BadCredentialsException("Incorrect username and password combination");
        }
    }

    public boolean verifyTokenHeader(final String header) {
        final var token = httpRequestService.getBearerToken(header);
        if (StringUtils.isNoneBlank(token)) {
            return tokenService.verifyToken(token);
        } else {
            return false;
        }
    }

    public void deleteToken(final String header) {
        final var token = httpRequestService.getBearerToken(header);
        if (StringUtils.isNoneBlank(token)) {
            final var jwtClaimsSet = tokenService.parseToken(token);
            tokenRepository.removeToken(jwtClaimsSet.getSubject());
        }
    }

    private User getUser(final String username) {
        try {
            final var record = userRepository.getUser(username);
            return conversionService.convert(record, User.class);
        } catch (NoDataFoundException e) {
            throw new BadCredentialsException("No user found for username " + username, e);
        }
    }
}
