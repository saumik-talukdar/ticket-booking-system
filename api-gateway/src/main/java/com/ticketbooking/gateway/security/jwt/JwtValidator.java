package com.ticketbooking.gateway.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtValidator {

    private final PublicKeyLoader publicKeyLoader;

    public JwtUser validateAndExtract(String token) {

        try {

            Claims claims = Jwts.parser()
                    .verifyWith(
                            (java.security.PublicKey)
                                    publicKeyLoader.getPublicKey()
                    )
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            UUID userId = UUID.fromString(
                    claims.getSubject()
            );

            String role = claims.get(
                    "role",
                    String.class
            );

            return new JwtUser(
                    userId,
                    role
            );

        } catch (Exception exception) {

            throw new InvalidJwtException(
                    "Invalid or expired JWT token",
                    exception
            );
        }
    }
}