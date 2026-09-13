package com.ticketbooking.identity.security.jwt;

import com.ticketbooking.identity.user.User;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.util.Date;

@Service
public class JwtService {

    private final KeyLoader keyLoader;
    private final long accessTokenExpiration;

    public JwtService(
            KeyLoader keyLoader,
            @Value("${jwt.access-token-expiration}")
            long accessTokenExpiration
    ) {
        this.keyLoader = keyLoader;
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public String generateAccessToken(User user) {

        PrivateKey privateKey = keyLoader.getPrivateKey();

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + accessTokenExpiration
        );

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }
}