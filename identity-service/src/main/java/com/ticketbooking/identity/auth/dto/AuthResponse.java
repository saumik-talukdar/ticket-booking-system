package com.ticketbooking.identity.auth.dto;

public record AuthResponse(

        String accessToken,

        String refreshToken,

        String tokenType

) {
}