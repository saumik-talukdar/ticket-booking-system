package com.ticketbooking.identity.security.jwt;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
public class KeyLoader {

    private final String privateKeyPath;

    private PrivateKey privateKey;

    public KeyLoader(
            @Value("${jwt.private-key-path}")
            String privateKeyPath
    ) {
        this.privateKeyPath = privateKeyPath;
    }

    @PostConstruct
    public void initialize() {

        this.privateKey = loadPrivateKey();
    }

    public PrivateKey getPrivateKey() {

        return privateKey;
    }

    private PrivateKey loadPrivateKey() {

        try {

            String privateKeyContent =
                    Files.readString(Path.of(privateKeyPath));

            String sanitizedKey = privateKeyContent
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes =
                    Base64.getDecoder().decode(sanitizedKey);

            PKCS8EncodedKeySpec keySpec =
                    new PKCS8EncodedKeySpec(keyBytes);

            KeyFactory keyFactory =
                    KeyFactory.getInstance("RSA");

            return keyFactory.generatePrivate(keySpec);

        } catch (
                IOException |
                InvalidKeySpecException |
                java.security.NoSuchAlgorithmException exception
        ) {

            throw new IllegalStateException(
                    "Failed to load RSA private key",
                    exception
            );
        }
    }
}