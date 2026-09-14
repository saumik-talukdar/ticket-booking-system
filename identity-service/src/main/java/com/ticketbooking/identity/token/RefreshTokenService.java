package com.ticketbooking.identity.token;

import com.ticketbooking.identity.exception.InvalidCredentialsException;
import com.ticketbooking.identity.exception.InvalidRefreshTokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RefreshTokenService {

    private static final String TOKEN_KEY_PREFIX = "refresh:token:";

    private static final String USED_KEY_PREFIX = "refresh:used:";

    private static final String USER_KEY_PREFIX = "refresh:user:";


    private final StringRedisTemplate redisTemplate;

    private final SecureRandom secureRandom;

    private final Duration refreshTokenExpiration;


    public RefreshTokenService(
            StringRedisTemplate redisTemplate,

            @Value("${jwt.refresh-token-expiration}")
            long refreshTokenExpiration
    ) {

        this.redisTemplate = redisTemplate;

        this.secureRandom = new SecureRandom();

        this.refreshTokenExpiration =
                Duration.ofMillis(
                        refreshTokenExpiration
                );
    }


    public String createRefreshToken(UUID userId) {

        String refreshToken = generateSecureToken();

        String tokenHash = hashToken(refreshToken);

        String tokenKey = buildTokenKey(tokenHash);

        String userKey = buildUserKey(userId);

        redisTemplate.opsForValue().set(
                tokenKey,
                userId.toString(),
                refreshTokenExpiration
        );

        redisTemplate.opsForSet().add(
                userKey,
                tokenHash
        );

        redisTemplate.expire(
                userKey,
                refreshTokenExpiration
        );

        return refreshToken;
    }

    public RefreshTokenRotationResult rotateRefreshToken(String oldRefreshToken) {

        String oldTokenHash = hashToken(oldRefreshToken);


        String oldTokenKey = buildTokenKey(oldTokenHash);

        String userIdStr = redisTemplate.opsForValue().getAndDelete(oldTokenKey);

        if (userIdStr == null) {

            checkAndHandleTokenReuse(oldTokenHash);

            throw new InvalidRefreshTokenException("Invalid or expired refresh token");
        }


        UUID userId = UUID.fromString(userIdStr);

        String userKey = buildUserKey(userId);

        redisTemplate.opsForValue().set(
                buildUsedKey(oldTokenHash),
                userIdStr,
                refreshTokenExpiration
        );

        redisTemplate.opsForSet().remove(
                userKey,
                oldTokenHash
        );

        String newRefreshToken = createRefreshToken(userId);

        return new RefreshTokenRotationResult(
                userId,
                newRefreshToken
        );
    }

    public void revokeRefreshToken(UUID userId, String refreshToken) {

        String tokenHash = hashToken(refreshToken);

        String tokenKey = buildTokenKey(tokenHash);

        String userIdStr = redisTemplate.opsForValue().get(tokenKey);

        redisTemplate.delete(tokenKey);

        if (userIdStr == null) {
            return;
        }

        UUID tokenUserId = UUID.fromString(userIdStr);

        if (!tokenUserId.equals(userId)) {

            throw new InvalidCredentialsException("Invalid refresh token");
        }

        String userKey = buildUserKey(tokenUserId);

        redisTemplate.opsForSet().remove(
                userKey,
                tokenHash
        );
    }

    public void revokeAllRefreshTokens(UUID userId) {

        String userKey = buildUserKey(userId);


        Set<String> tokenHashes = redisTemplate.opsForSet().members(userKey);

        if (tokenHashes != null && !tokenHashes.isEmpty()) {

            Set<String> tokenKeys =
                    tokenHashes.stream()
                            .map(this::buildTokenKey)
                            .collect(Collectors.toSet());
            redisTemplate.delete(tokenKeys);
        }

        redisTemplate.delete(userKey);
    }

    private void checkAndHandleTokenReuse(String tokenHash) {

        String usedTokenKey = buildUsedKey(tokenHash);

        String userIdStr = redisTemplate.opsForValue().get(usedTokenKey);

        if (userIdStr == null) {
            return;
        }

        UUID userId = UUID.fromString(userIdStr);

        revokeAllRefreshTokens(userId);

        redisTemplate.delete(usedTokenKey);

        throw new InvalidRefreshTokenException(
                "Refresh token reuse detected. All active sessions have been revoked."
        );
    }


    private String buildTokenKey(String tokenHash) {

        return TOKEN_KEY_PREFIX + tokenHash;
    }


    private String buildUsedKey(String tokenHash) {

        return USED_KEY_PREFIX + tokenHash;
    }


    private String buildUserKey(UUID userId) {

        return USER_KEY_PREFIX + userId;
    }


    private String generateSecureToken() {

        byte[] randomBytes = new byte[64];

        secureRandom.nextBytes(randomBytes);

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);
    }

    private String hashToken(String refreshToken) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");


            byte[] hash = digest.digest(refreshToken.getBytes(StandardCharsets.UTF_8));

            return Base64
                    .getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

        } catch (NoSuchAlgorithmException exception) {

            throw new IllegalStateException(
                    "SHA-256 algorithm unavailable",
                    exception
            );
        }
    }
}