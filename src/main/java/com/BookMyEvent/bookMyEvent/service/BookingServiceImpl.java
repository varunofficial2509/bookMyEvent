package com.BookMyEvent.bookMyEvent.service;


import com.BookMyEvent.bookMyEvent.dto.BookingDTO;
import com.BookMyEvent.bookMyEvent.dto.TicketDTO;
import com.BookMyEvent.bookMyEvent.entity.*;
import com.BookMyEvent.bookMyEvent.exception.*;
import com.BookMyEvent.bookMyEvent.model.BookingStatus;
import com.BookMyEvent.bookMyEvent.model.EventType;
import com.BookMyEvent.bookMyEvent.model.SeatStatus;
import com.BookMyEvent.bookMyEvent.model.TicketStatus;
import com.BookMyEvent.bookMyEvent.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl {

    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final TicketCategoryRepository ticketCategoryRepository;

    @Autowired
    public BookingServiceImpl(
            SeatRepository seatRepository,
            ShowRepository showRepository,
            UserRepository userRepository,
            EventRepository eventRepository,
            BookingRepository bookingRepository,
            TicketRepository ticketRepository,
            TicketCategoryRepository ticketCategoryRepository) {
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.ticketCategoryRepository = ticketCategoryRepository;
    }

    /**
     * Book seats for indoor events (with seat selection)
     */
    public BookingDTO bookIndoorEvent(Long userId, Long showId, List<Long> seatIds) {
        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Validate show
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ShowNotFoundException("Show not found with id: " + showId));

        Event event = show.getEvent();
        if (event.getEventType() != EventType.INDOOR) {
            throw new InvalidEventTypeException("This event does not support seat selection");
        }

        // Fetch and lock seats with pessimistic locking
        List<Seat> seats = seatRepository.findByIdInWithLock(seatIds);

        if (seats.size() != seatIds.size()) {
            throw new SeatNotFoundException("One or more seats not found");
        }

        // Validate all seats belong to the same show
        boolean allSeatsValid = seats.stream()
                .allMatch(seat -> seat.getShow().getId().equals(showId));
        if (!allSeatsValid) {
            throw new InvalidSeatException("All seats must belong to the same show");
        }

        // Check seat availability and update status
        validateAndBlockSeats(seats, userId);

        try {
            // Create booking
            Booking booking = createBooking(user, event, show, seats.size());

            // Create tickets for each seat
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (Seat seat : seats) {
                Ticket ticket = createTicketForSeat(booking, seat);
                booking.addTicket(ticket);
                totalAmount = totalAmount.add(ticket.getPrice());

                // Update seat status to BOOKED
                seat.setSeatStatus(SeatStatus.BOOKED);
                seatRepository.save(seat);
            }

            booking.setTotalAmount(totalAmount);
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            return convertToDTO(booking);

        } catch (OptimisticLockException e) {
            throw new SeatBlockedException("Seats were booked by another user. Please try again.");
        }
    }

    /**
     * Book tickets for outdoor events (type-based tickets)
     */
    public BookingDTO bookOutdoorEvent(Long userId, Long eventId, Map<Long, Integer> ticketCategoryQuantities) {
        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Validate event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found with id: " + eventId));

        if (event.getEventType() != EventType.OUTDOOR) {
            throw new InvalidEventTypeException("This event requires seat selection");
        }

        // Validate and lock ticket categories
        List<Long> categoryIds = new ArrayList<>(ticketCategoryQuantities.keySet());
        List<TicketCategory> ticketCategories = ticketCategoryRepository.findByIdInWithLock(categoryIds);

        if (ticketCategories.size() != categoryIds.size()) {
            throw new TicketCategoryNotFoundException("One or more ticket categories not found");
        }

        // Check availability and reduce quantities
        for (TicketCategory category : ticketCategories) {
            Integer requestedQuantity = ticketCategoryQuantities.get(category.getId());

            if (category.getAvailableQuantity() < requestedQuantity) {
                throw new InsufficientTicketsException(
                        String.format("Only %d tickets available for %s",
                                category.getAvailableQuantity(),
                                category.getName())
                );
            }

            // Reduce available quantity
            category.setAvailableQuantity(category.getAvailableQuantity() - requestedQuantity);
            ticketCategoryRepository.save(category);
        }

        try {
            // Calculate total tickets
            int totalTickets = ticketCategoryQuantities.values().stream()
                    .mapToInt(Integer::intValue)
                    .sum();

            // Create booking
            Booking booking = createBooking(user, event, null, totalTickets);

            // Create tickets for each category
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (TicketCategory category : ticketCategories) {
                Integer quantity = ticketCategoryQuantities.get(category.getId());

                for (int i = 0; i < quantity; i++) {
                    Ticket ticket = createTicketForCategory(booking, category);
                    booking.addTicket(ticket);
                    totalAmount = totalAmount.add(ticket.getPrice());
                }
            }

            booking.setTotalAmount(totalAmount);
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            return convertToDTO(booking);

        } catch (OptimisticLockException e) {
            throw new ConcurrentBookingException("Tickets were booked by another user. Please try again.");
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
                    // Check if lock has expired
                    if (seat.getLockedUntil() != null && seat.getLockedUntil().isAfter(now)) {
                        if (!userId.equals(seat.getLockedByUserId())) {
                            throw new SeatBlockedException(
                                    "Seat " + seat.getSeatNumber() + " is temporarily blocked"
                            );
                        }
                    } else {
                        // Lock expired, make it available
                        seat.setSeatStatus(SeatStatus.AVAILABLE);
                    }
                    break;

                case RESERVED:
                    throw new SeatReservedException(
                            "Seat " + seat.getSeatNumber() + " is reserved"
                    );

                case AVAILABLE:
                    // Temporarily block the seat
                    seat.setSeatStatus(SeatStatus.BLOCKED);
                    seat.setLockedUntil(now.plusMinutes(10)); // 10 min lock
                    seat.setLockedByUserId(userId);
                    break;
            }
        }
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
        ticket.setSeat(null); // No seat for outdoor events
        ticket.setTicketNumber(generateTicketNumber());
        ticket.setPrice(category.getPrice());
        ticket.setStatus(TicketStatus.ACTIVE);
        ticket.setCreatedAt(LocalDateTime.now());
        return ticket;
    }

    private String generateBookingReference() {
        return "BKG" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateTicketNumber() {
        return "TKT" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setBookingId(booking.getId());
        dto.setBookingReferenceId(booking.getBookingReference());
        dto.setUserId(booking.getUser().getId());
        dto.setEventId(booking.getEvent().getId());
        dto.setEventName(booking.getEvent().getName());
        dto.setShowId(booking.getShow() != null ? booking.getShow().getId() : null);
        dto.set(booking.getNumberOfTickets());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setStatus(booking.getStatus());
        dto.setBookingDate(booking.getBookingDate());

        List<TicketDTO> ticketDTOs = booking.getTickets().stream()
                .map(this::convertTicketToDTO)
                .collect(Collectors.toList());
        dto.setTickets(ticketDTOs);

        return dto;
    }

    private TicketDTO convertTicketToDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setTicketNumber(ticket.getTicketNumber());
        dto.setTicketCategoryName(ticket.getTicketCategory().getName());
        dto.setTicketType(ticket.getTicketCategory().getTicketType());
        dto.setSeatNumber(ticket.getSeat() != null ? ticket.getSeat().getSeatNumber() : null);
        dto.setPrice(ticket.getPrice());
        dto.setStatus(ticket.getStatus());
        return dto;
    }
}
