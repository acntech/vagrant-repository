package no.acntech.service;

import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.security.SecureRandom;

import no.acntech.model.Password;

@Validated
@Service
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public PasswordService(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Password createPassword(@NotBlank String plaintextPassword) {
        var random = new SecureRandom();
        var salt = new byte[16];
        random.nextBytes(salt);
        var hex = Hex.encode(salt);
        var passwordSalt = String.valueOf(hex);
        var passwordHash = passwordEncoder.encode(passwordSalt + plaintextPassword);
        return new Password(passwordHash, passwordSalt);
    }

    public boolean verifyPassword(@NotBlank String plaintextPassword, @Valid @NotNull Password password) {
        return passwordEncoder.matches(password.passwordSalt() + plaintextPassword, password.passwordHash());
    }
}
