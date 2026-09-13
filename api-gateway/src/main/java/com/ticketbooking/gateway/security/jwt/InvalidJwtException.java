package com.ticketbooking.gateway.security.jwt;

public class InvalidJwtException extends RuntimeException {

    public InvalidJwtException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}