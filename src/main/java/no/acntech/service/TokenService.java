package no.acntech.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Validated
@Service
public class TokenService {

    public static final String ROLE_CLAIM_NAME = "role";
    private static final String TOKEN_CONTENT_TYPE = "JWT";
    private final KeyService keyService;

    public TokenService(final KeyService keyService) {
        this.keyService = keyService;
    }

    public SignedJWT createToken(@NotBlank String username,
                                 @NotNull List<String> roles) {
        try {
            final var privateKey = keyService.privateRsaKey();
            final var signer = new RSASSASigner(privateKey);
            final var jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .contentType(TOKEN_CONTENT_TYPE)
                    .keyID(privateKey.getKeyID())
                    .build();
            final var jwtClaims = new JWTClaimsSet.Builder()
                    .jwtID(UUID.randomUUID().toString())
                    .issuer("http:/localhost:8080")
                    .issueTime(new Date())
                    .subject(username)
                    .claim(ROLE_CLAIM_NAME, roles)
                    .build();
            final var signedJWT = new SignedJWT(jwsHeader, jwtClaims);
            signedJWT.sign(signer);
            return signedJWT;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public JWTClaimsSet parseToken(@NotBlank String token) {
        try {
            final var signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verifyToken(String token) {
        try {
            final var signedJWT = SignedJWT.parse(token);
            final var publicKey = keyService.publicRsaKey();
            final var verifier = new RSASSAVerifier(publicKey);
            return signedJWT.verify(verifier);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ZonedDateTime getIssuedDateTime(final SignedJWT signedJWT) {
        try {
            final var issuedDate = signedJWT.getJWTClaimsSet().getIssueTime();
            return ZonedDateTime.ofInstant(issuedDate.toInstant(), ZoneId.systemDefault());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
