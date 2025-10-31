package com.BookMyEvent.bookMyEvent.service;


import com.BookMyEvent.bookMyEvent.dto.BookingDTO;
import com.BookMyEvent.bookMyEvent.dto.ConfirmedBookingEvent;
import com.BookMyEvent.bookMyEvent.entity.*;
import com.BookMyEvent.bookMyEvent.exception.*;
import com.BookMyEvent.bookMyEvent.mapper.BookingMapper;
import com.BookMyEvent.bookMyEvent.model.*;
import com.BookMyEvent.bookMyEvent.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingService {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final TicketCategoryRepository ticketCategoryRepository;
    private final BookingMapper bookingMapper; // Inject mapper

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingDTO bookIndoorEvent(Long userId, Long showId, List<Long> seatIds) {

        User user = findUserOrThrow(userId);
        Show show = findShowOrThrow(showId);
        Event event = show.getEvent();
        validateIndoorEvent(event);

        // Fetch and Validate
        List<Seat> seats = fetchAndValidateSeats(seatIds, showId);

        // Seat availability and Blocking
        validateAndBlockSeats(seats, userId);

        try {
            Booking booking = createBooking(user, event, show, seats.size());
            BigDecimal totalAmount = processSeatsBooking(booking, seats);

            booking.setTotalAmount(totalAmount);
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.saveAndFlush(booking);
            BookingDTO bookingDTO = bookingMapper.toDTO(booking);

            // Publish event - will be processed after transaction commits
            applicationEventPublisher.publishEvent(
                    new ConfirmedBookingEvent(bookingDTO)
            );

            return bookingDTO;

        } catch (OptimisticLockException e) {
            throw new SeatBlockedException("Seats were booked by another user. Please try again.");
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingDTO bookOutdoorEvent(Long userId, Long eventId,
                                       Map<Long, Integer> ticketCategoryQuantities) {
        User user = findUserOrThrow(userId);
        Event event = findEventOrThrow(eventId);
        validateOutdoorEvent(event);
        List<TicketCategory> categories = fetchAndValidateCategories(
                new ArrayList<>(ticketCategoryQuantities.keySet())
        );
        validateAndReduceTicketQuantities(categories, ticketCategoryQuantities);

        try {
            int totalTickets = ticketCategoryQuantities.values().stream()
                    .mapToInt(Integer::intValue).sum();

            Booking booking = createBooking(user, event, null, totalTickets);
            BigDecimal totalAmount = processCategoryBooking(booking, categories,
                    ticketCategoryQuantities);

            booking.setTotalAmount(totalAmount);
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.saveAndFlush(booking);
            BookingDTO bookingDTO = bookingMapper.toDTO(booking);

            // Publish event - will be processed after transaction commits
            applicationEventPublisher.publishEvent(
                    new ConfirmedBookingEvent(bookingDTO)
            );

            return bookingDTO;

        } catch (OptimisticLockException e) {
            throw new ConcurrentBookingException("Tickets were booked by another user. Please try again.");
        }
    }

    // ========== Helper Methods (Business Logic) ==========

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
    }

    private Show findShowOrThrow(Long showId) {
        return showRepository.findById(showId)
                .orElseThrow(() -> new ShowNotFoundException("Show not found with id: " + showId));
    }

    private Event findEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));
    }

    private void validateIndoorEvent(Event event) {
        if (event.getEventType() != EventType.INDOOR) {
            throw new InvalidEventTypeException("This event does not support seat selection");
        }
    }

    private void validateOutdoorEvent(Event event) {
        if (event.getEventType() != EventType.OUTDOOR) {
            throw new InvalidEventTypeException("This event requires seat selection");
        }
    }

    private List<Seat> fetchAndValidateSeats(List<Long> seatIds, Long showId) {
        List<Seat> seats = seatRepository.findByIdInWithLock(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new SeatNotFoundException("One or more seats not found");
        }
        return seats;
    }

    private List<TicketCategory> fetchAndValidateCategories(List<Long> categoryIds) {
        List<TicketCategory> categories = ticketCategoryRepository.findByIdInWithLock(categoryIds);

        if (categories.size() != categoryIds.size()) {
            throw new TicketCategoryNotFoundException("One or more ticket categories not found");
        }

        return categories;
    }

    private void validateAndReduceTicketQuantities(List<TicketCategory> categories,
                                                   Map<Long, Integer> quantities) {
        for (TicketCategory category : categories) {
            Integer requested = quantities.get(category.getId());

            if (category.getAvailableQuantity() < requested) {
                throw new InsufficientTicketsException(
                        String.format("Only %d tickets available for %s",
                                category.getAvailableQuantity(), category.getName())
                );
            }

            category.setAvailableQuantity(category.getAvailableQuantity() - requested);
            ticketCategoryRepository.save(category);
        }
    }

    private void validateAndBlockSeats(List<Seat> seats, Long userId) {
        LocalDateTime now = LocalDateTime.now();

        for (Seat seat : seats) {
            SeatStatus status = seat.getSeatStatus();

            switch (status) {
                case BOOKED:
                    throw new SeatAlreadyBookedException(
                            "Seat " + seat.getSeatNumber() + " is already booked"
                    );

                case BLOCKED:
                    if (seat.getLockedUntil() != null && seat.getLockedUntil().isAfter(now)) {
                        if (!userId.equals(seat.getLockedByUserId())) {
                            throw new SeatBlockedException(
                                    "Seat " + seat.getSeatNumber() + " is temporarily blocked"
                            );
                        }
                    } else {
                        seat.setSeatStatus(SeatStatus.AVAILABLE);
                    }
                    break;

                case RESERVED:
                    throw new SeatReservedException(
                            "Seat " + seat.getSeatNumber() + " is reserved"
                    );

                case AVAILABLE:
                    seat.setSeatStatus(SeatStatus.BLOCKED);
                    seat.setLockedUntil(now.plusMinutes(10));
                    seat.setLockedByUserId(userId);
                    break;
            }
        }
    }

    private BigDecimal processSeatsBooking(Booking booking, List<Seat> seats) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Seat seat : seats) {
            Ticket ticket = createTicketForSeat(booking, seat);
            booking.addTicket(ticket);
            totalAmount = totalAmount.add(ticket.getPrice());

            seat.setSeatStatus(SeatStatus.BOOKED);
            seatRepository.saveAndFlush(seat);
        }

        return totalAmount;
    }

    private BigDecimal processCategoryBooking(Booking booking,
                                              List<TicketCategory> categories,
                                              Map<Long, Integer> quantities) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (TicketCategory category : categories) {
            Integer quantity = quantities.get(category.getId());

            for (int i = 0; i < quantity; i++) {
                Ticket ticket = createTicketForCategory(booking, category);
                booking.addTicket(ticket);
                totalAmount = totalAmount.add(ticket.getPrice());
            }
        }

        return totalAmount;
    }

    private Booking createBooking(User user, Event event, Show show, int numberOfTickets) {
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEvent(event);
        booking.setShow(show);
        booking.setBookingReference(generateBookingReference());
        booking.setNumberOfTickets(numberOfTickets);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);
        return booking;
    }

    private Ticket createTicketForSeat(Booking booking, Seat seat) {
        Ticket ticket = new Ticket();
        ticket.setBooking(booking);
        ticket.setTicketCategory(seat.getTicketCategory());
        ticket.setSeat(seat);
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setPrice(seat.getTicketCategory().getPrice());
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket.setCreatedAt(LocalDateTime.now());
        return ticket;
    }

    private Ticket createTicketForCategory(Booking booking, TicketCategory category) {
        Ticket ticket = new Ticket();
        ticket.setBooking(booking);
        ticket.setTicketCategory(category);
        ticket.setSeat(null);
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setPrice(category.getPrice());
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket.setCreatedAt(LocalDateTime.now());
        return ticket;
    }

    private String generateBookingReference() {
        return "BKG" + System.currentTimeMillis() +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateTicketNumber() {
        return "TKT" + System.currentTimeMillis() +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
