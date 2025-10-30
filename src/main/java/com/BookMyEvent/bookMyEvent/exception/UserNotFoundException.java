package com.BookMyEvent.bookMyEvent.exception;

public class UserNotFoundException extends BookingFailedException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
