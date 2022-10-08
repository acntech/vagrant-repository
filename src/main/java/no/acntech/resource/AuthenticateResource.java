package no.acntech.resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import no.acntech.model.Login;
import no.acntech.model.Token;
import no.acntech.service.AuthenticateService;

@RequestMapping(path = "/api/v1/authenticate")
@RestController
public class AuthenticateResource {

    private final AuthenticateService authenticateService;

    public AuthenticateResource(final AuthenticateService authenticateService) {
        this.authenticateService = authenticateService;
    }

    @PostMapping
    public ResponseEntity<Token> createToken(@RequestBody @Valid @NotNull final Login login) {
        final var token = authenticateService.createToken(login);
        return ResponseEntity.ok(token);
    }

    @GetMapping
    public ResponseEntity<Void> validateToken(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) final String header) {
        if (authenticateService.verifyTokenHeader(header)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Token> deleteToken(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) final String header) {
        authenticateService.deleteToken(header);
        return ResponseEntity.ok().build();
    }
}
