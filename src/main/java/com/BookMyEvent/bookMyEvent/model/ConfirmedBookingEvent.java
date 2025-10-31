package com.BookMyEvent.bookMyEvent.model;

import com.BookMyEvent.bookMyEvent.dto.BookingDTO;
import com.BookMyEvent.bookMyEvent.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


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
