package com.ticketbooking.identity.auth.dto;

import com.ticketbooking.identity.user.Role;

import java.util.UUID;

public record RegisterResponse(

        UUID userId,

        String email,

        Role role

) {
}