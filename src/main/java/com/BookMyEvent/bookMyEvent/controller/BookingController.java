package com.BookMyEvent.bookMyEvent.controller;

import com.BookMyEvent.bookMyEvent.dto.BookingDTO;
import com.BookMyEvent.bookMyEvent.dto.ErrorResponseDTO;
import com.BookMyEvent.bookMyEvent.dto.IndoorBookingRequestDTO;
import com.BookMyEvent.bookMyEvent.dto.OutdoorBookingRequestDTO;
import com.BookMyEvent.bookMyEvent.exception.BookingException;
import com.BookMyEvent.bookMyEvent.service.BookingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingServiceImpl bookingService;

    @Autowired
    public BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/indoor")
    public ResponseEntity<BookingDTO> bookIndoorEvent(@RequestBody IndoorBookingRequestDTO request) {
        try {
            BookingDTO booking = bookingService.bookIndoorEvent(
                    request.getUserId(),
                    request.getShowId(),
                    request.getSeatIds()
            );
            return ResponseEntity.ok(booking);
        } catch (BookingException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/outdoor")
    public ResponseEntity<BookingDTO> bookOutdoorEvent(@RequestBody OutdoorBookingRequestDTO request) {
        try {
            BookingDTO booking = bookingService.bookOutdoorEvent(
                    request.getUserId(),
                    request.getEventId(),
                    request.getTicketCategoryQuantities()
            );
            return ResponseEntity.ok(booking);
        } catch (BookingException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{bookingReference}")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable String bookingReference) {
        // Implementation needed
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@PathVariable Long userId) {
        // Implementation needed
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(BookingException.class)
    public ResponseEntity<ErrorResponseDTO> handleBookingException(BookingException e) {
        ErrorResponseDTO error = new ErrorResponseDTO(e.getMessage(), LocalDateTime.now());
        return ResponseEntity.badRequest().body(error);
    }
}

