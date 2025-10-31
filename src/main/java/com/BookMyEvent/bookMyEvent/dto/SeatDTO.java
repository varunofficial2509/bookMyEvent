package com.BookMyEvent.bookMyEvent.dto;

import com.BookMyEvent.bookMyEvent.entity.Seat;
import com.BookMyEvent.bookMyEvent.model.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatDTO {

    private Long id;

    private Long showId;

    private String seatNumber;

    private SeatStatus seatStatus;

    private Long ticketCategoryId;

    private String ticketCategoryName;

    private BigDecimal price;

    private Long version;

    private LocalDateTime lockedUntil;

    private Long lockedByUserId;

    private Boolean isAvailable;

    private Boolean isLocked;

    public SeatDTO(Seat seat) {
        this.id = seat.getId();
        this.showId = seat.getShow().getId();
        this.seatNumber = seat.getSeatNumber();
        this.seatStatus = seat.getSeatStatus();
        this.ticketCategoryId = seat.getTicketCategory().getId();
        this.ticketCategoryName = seat.getTicketCategory().getName();
        this.price = seat.getTicketCategory().getPrice();
        this.version = seat.getVersion();
        this.lockedUntil = seat.getLockedUntil();
        this.lockedByUserId = seat.getLockedByUserId();
        this.isAvailable = seat.getSeatStatus() == SeatStatus.AVAILABLE &&
                (seat.getLockedUntil() == null || seat.getLockedUntil().isBefore(LocalDateTime.now()));
        this.isLocked = seat.getLockedUntil() != null && seat.getLockedUntil().isAfter(LocalDateTime.now());
    }
}

