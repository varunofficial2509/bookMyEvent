package com.BookMyEvent.bookMyEvent.entity;

import com.BookMyEvent.bookMyEvent.model.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@Getter
@Setter
@Table(name = "seats", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"show_id", "seat_number"})
})
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Column(nullable = false, length = 20)
    private String seatNumber; // e.g., "A1", "B12"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus seatStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_category_id", nullable = false)
    private TicketCategory ticketCategory;

    @Version
    private Long version; // Critical for optimistic locking

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil; // For temporary seat blocking during booking

    @Column(name = "locked_by_user_id")
    private Long lockedByUserId;

    public Seat() {
        this.seatStatus = SeatStatus.AVAILABLE;
    }

    public Seat(Show show, String seatNumber, TicketCategory ticketCategory) {
        this.show = show;
        this.seatNumber = seatNumber;
        this.ticketCategory = ticketCategory;
        this.seatStatus = SeatStatus.AVAILABLE;
    }
}
