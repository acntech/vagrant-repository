package no.acntech.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Service
public class KeyService {

    private static final String KEY_ID = UUID.randomUUID().toString();
    private final Resource publicKeyFile;
    private final Resource privateKeyFile;
    private final KeyFactory rsaKeyFactory;

    public KeyService(@Value("${acntech.keys.public-key}") final Resource publicKeyFile,
                      @Value("${acntech.keys.private-key}") final Resource privateKeyFile) throws Exception {
        this.publicKeyFile = publicKeyFile;
        this.privateKeyFile = privateKeyFile;
        this.rsaKeyFactory = KeyFactory.getInstance("RSA");
    }

    public RSAKey publicRsaKey() throws Exception {
        return new RSAKey.Builder(publicKey())
                .keyID(KEY_ID)
                .keyUse(KeyUse.SIGNATURE)
                .build();
    }

    public RSAKey privateRsaKey() throws Exception {
        return new RSAKey.Builder(publicKey())
                .privateKey(privateKey())
                .keyID(KEY_ID)
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .build();
    }

    private RSAPublicKey publicKey() throws Exception {
        try (final var inputStream = publicKeyFile.getInputStream()) {
            String publicKey = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey);
            return (RSAPublicKey) rsaKeyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        }
    }

    private RSAPrivateKey privateKey() throws Exception {
        try (final var inputStream = privateKeyFile.getInputStream()) {
            String privateKey = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey);
            return (RSAPrivateKey) rsaKeyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        }

    }
}
