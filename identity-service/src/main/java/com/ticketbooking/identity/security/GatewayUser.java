package com.ticketbooking.identity.security;

import com.ticketbooking.identity.user.Role;

import java.util.UUID;

public record GatewayUser(
        UUID userId,
        Role role
) {
}