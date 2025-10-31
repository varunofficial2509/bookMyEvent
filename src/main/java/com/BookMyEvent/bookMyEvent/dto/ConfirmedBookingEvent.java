package com.BookMyEvent.bookMyEvent.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
public class ConfirmedBookingEvent {
    private BookingDTO bookingDetails;
    private LocalDateTime eventTime;
    public ConfirmedBookingEvent(BookingDTO bookingDetails) {
        this.bookingDetails = bookingDetails;
        this.eventTime = LocalDateTime.now();
    }
}
