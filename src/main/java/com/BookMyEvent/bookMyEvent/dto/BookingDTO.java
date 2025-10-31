package com.BookMyEvent.bookMyEvent.dto;

import com.BookMyEvent.bookMyEvent.entity.Booking;
import com.BookMyEvent.bookMyEvent.entity.Ticket;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for Booking entity.
 * Carries only relevant data for client responses,
 * avoiding entity exposure and circular references.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO implements Serializable {

    private Long bookingId;
    private String bookingReferenceId;
    private Long userId;
    private String userEmail;
    private String userName;
    private Long eventId;
    private String eventName;
    private Long showId;
    private LocalDateTime showTime;
    private String venueName;
    private String cityName;
    private LocalDateTime bookingTime;
    private List<String> seatNumbers;

    // Optional constructor for lightweight projection queries
    public BookingDTO(Long bookingId,
                      String bookingReferenceId,
                      String userEmail,
                      String userName,
                      List<String> seatNumbers,
                      String eventName,
                      LocalDateTime bookingTime,
                      String venueName,
                      String cityName,
                      LocalDateTime showTime) {
        this.bookingId = bookingId;
        this.bookingReferenceId = bookingReferenceId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.seatNumbers = seatNumbers;
        this.eventName = eventName;
        this.bookingTime = bookingTime;
        this.venueName = venueName;
        this.cityName = cityName;
        this.showTime = showTime;
    }

    // Add this constructor to BookingDTO class
    public BookingDTO(Booking booking) {
        this.bookingId = booking.getId();
        this.bookingReferenceId = booking.getBookingReference();
        this.userId = booking.getUser().getId();
        this.userEmail = booking.getUser().getEmail();
        this.userName = booking.getUser().getFirstName();
        this.eventId = booking.getEvent().getId();
        this.eventName = booking.getEvent().getName();

        // Handle optional show (for outdoor events without shows)
        if (booking.getShow() != null) {
            this.showId = booking.getShow().getId();
            this.showTime = booking.getShow().getStartTime();
            this.venueName = booking.getShow().getVenue().getVenueName();
            this.cityName = booking.getShow().getVenue().getCity().getCityName();
        }

        this.bookingTime = booking.getBookingDate();

        // Map tickets to seat numbers
        // Note: seat is nullable (only for indoor events with seat selection)
        this.seatNumbers = booking.getTickets().stream()
                .filter(ticket -> ticket.getSeat() != null)
                .map(ticket -> ticket.getSeat().getSeatNumber())
                .collect(Collectors.toList());
    }

}
