package com.ticketbooking.identity.token;

import java.util.UUID;

public record RefreshTokenRotationResult(
        UUID userId,
        String refreshToken
) {
}