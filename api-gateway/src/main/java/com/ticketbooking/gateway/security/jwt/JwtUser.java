package com.ticketbooking.gateway.security.jwt;

import java.util.UUID;

public record JwtUser(
        UUID userId,
        String role
) {
}