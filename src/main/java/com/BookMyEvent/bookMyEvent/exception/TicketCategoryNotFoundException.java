package com.BookMyEvent.bookMyEvent.exception;

public class TicketCategoryNotFoundException extends RuntimeException {
    public TicketCategoryNotFoundException(String message) {
        super(message);
    }
}
