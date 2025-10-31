package com.BookMyEvent.bookMyEvent.mapper;

import com.BookMyEvent.bookMyEvent.dto.BookingDTO;
import com.BookMyEvent.bookMyEvent.dto.TicketDTO;
import com.BookMyEvent.bookMyEvent.entity.Booking;
import com.BookMyEvent.bookMyEvent.entity.Ticket;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public BookingDTO toDTO(Booking booking) {
        BookingDTO dto = BookingDTO.builder()
                .bookingId(booking.getId())
                .bookingReferenceId(booking.getBookingReference())
                .userId(booking.getUser().getId())
                .userEmail(booking.getUser().getEmail())
                .userName(booking.getUser().getFirstName())
                .eventId(booking.getEvent().getId())
                .eventName(booking.getEvent().getName())
                .bookingTime(booking.getBookingDate())
                .build();

        // Handle optional show
        if (booking.getShow() != null) {
            dto.setShowId(booking.getShow().getId());
            dto.setShowTime(booking.getShow().getStartTime());
            dto.setVenueName(booking.getShow().getVenue().getVenueName());
            dto.setCityName(booking.getShow().getVenue().getCity().getCityName());
        }

        // Map tickets
        dto.setTickets(booking.getTickets().stream()
                .map(this::toTicketDTO)
                .collect(Collectors.toList()));

        // Map seat numbers
        dto.setSeatNumbers(booking.getTickets().stream()
                .filter(ticket -> ticket.getSeat() != null)
                .map(ticket -> ticket.getSeat().getSeatNumber())
                .collect(Collectors.toList()));

        return dto;
    }

    public TicketDTO toTicketDTO(Ticket ticket) {
        return TicketDTO.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .ticketCategoryName(ticket.getTicketCategory() != null ?
                        ticket.getTicketCategory().getName() : null)
                .ticketType(ticket.getTicketCategory() != null && ticket.getTicketCategory().getTicketType() != null ?
                        ticket.getTicketCategory().getTicketType() : null)
                .seatNumber(ticket.getSeat() != null ?
                        ticket.getSeat().getSeatNumber() : null)
                .price(ticket.getPrice())
                .status(ticket.getStatus())
                .build();
    }
}
