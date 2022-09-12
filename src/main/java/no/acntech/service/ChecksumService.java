package no.acntech.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import no.acntech.exception.StorageException;
import no.acntech.model.Algorithm;

@Validated
@Service
public class ChecksumService {

    public String generateChecksum(@NotNull final Path filePath,
                                   @NotNull final Algorithm algorithm) {
        if (!Files.exists(filePath)) {
            throw new StorageException("File already exists");
        }

        try (FileInputStream fileInputStream = new FileInputStream(filePath.toFile())) {
            switch (algorithm) {
                case MD5 -> {
                    return DigestUtils.md5Hex(fileInputStream);
                }
                case SHA1 -> {
                    return DigestUtils.sha1Hex(fileInputStream);
                }
                case SHA256 -> {
                    return DigestUtils.sha256Hex(fileInputStream);
                }
                case SHA384 -> {
                    return DigestUtils.sha384Hex(fileInputStream);
                }
                case SHA512 -> {
                    return DigestUtils.sha512Hex(fileInputStream);
                }
                default -> throw new IllegalArgumentException("Unsupported algorithm");
            }
        } catch (IOException e) {
            throw new StorageException("Could not read file", e);
        }
    }
}
