package com.ticketbooking.identity.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(

        UUID id,

        String email,

        String firstName,

        String lastName,

        String role,

        String status,

        Instant createdAt

) {
}