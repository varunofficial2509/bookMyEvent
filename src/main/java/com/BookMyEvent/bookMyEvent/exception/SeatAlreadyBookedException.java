package com.BookMyEvent.bookMyEvent.exception;

public class SeatAlreadyBookedException extends BookingFailedException {
    public SeatAlreadyBookedException(String message) {
        super(message);
    }
}
