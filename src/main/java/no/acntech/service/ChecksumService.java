package no.acntech.service;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import no.acntech.model.Algorithm;

@Validated
@Service
public class ChecksumService {

    private final MessageDigest sha256MessageDigest;
    private final MessageDigest sha512MessageDigest;

    public ChecksumService() throws NoSuchAlgorithmException {
        this.sha256MessageDigest = MessageDigest.getInstance("SHA-256");
        this.sha512MessageDigest = MessageDigest.getInstance("SHA-512");
    }

    public String generateChecksum(@NotNull final Algorithm algorithm,
                                   @NotBlank final String content) {
        final var input = content.getBytes(StandardCharsets.UTF_8);
        switch (algorithm) {
            case SHA256 -> {
                final var hash = sha256MessageDigest.digest(input);
                return Hex.encodeHexString(hash);
            }
            case SHA512 -> {
                final var hash = sha512MessageDigest.digest(input);
                return Hex.encodeHexString(hash);
            }
            default -> throw new IllegalArgumentException("Unsupported algorithm");
        }
    }
}
