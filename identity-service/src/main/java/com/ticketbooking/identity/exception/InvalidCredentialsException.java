package com.ticketbooking.identity.exception;

public class InvalidCredentialsException
        extends RuntimeException {

    public InvalidCredentialsException(
            String message
    ) {
        super(message);
    }
}