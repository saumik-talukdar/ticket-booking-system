package com.ticketbooking.identity.exception;

public class UserAlreadyExistsException
        extends RuntimeException {

    public UserAlreadyExistsException(
            String message
    ) {
        super(message);
    }
}