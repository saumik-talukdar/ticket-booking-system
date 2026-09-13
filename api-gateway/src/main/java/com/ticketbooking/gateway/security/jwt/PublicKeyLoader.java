package com.ticketbooking.gateway.security.jwt;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class PublicKeyLoader {

    private final String publicKeyPath;

    private PublicKey publicKey;

    public PublicKeyLoader(
            @Value("${jwt.public-key-path}")
            String publicKeyPath
    ) {
        this.publicKeyPath = publicKeyPath;
    }

    @PostConstruct
    public void initialize() {
        this.publicKey = loadPublicKey();
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    private PublicKey loadPublicKey() {

        try {

            String publicKeyContent =
                    Files.readString(Path.of(publicKeyPath));

            String sanitizedKey = publicKeyContent
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes =
                    Base64.getDecoder().decode(sanitizedKey);

            X509EncodedKeySpec keySpec =
                    new X509EncodedKeySpec(keyBytes);

            KeyFactory keyFactory =
                    KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(keySpec);

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Failed to load RSA public key",
                    exception
            );
        }
    }
}