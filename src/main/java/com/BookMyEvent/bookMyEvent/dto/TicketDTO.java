package com.BookMyEvent.bookMyEvent.dto;

import com.BookMyEvent.bookMyEvent.entity.Ticket;
import com.BookMyEvent.bookMyEvent.model.TicketStatus;
import com.BookMyEvent.bookMyEvent.model.TicketType;
import lombok.*;

import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TicketDTO {
    private Long id;
    private String ticketNumber;
    private String ticketCategoryName;
    private TicketType ticketType;
    private String seatNumber; // Null for outdoor events
    private BigDecimal price;
    private TicketStatus status;

    public TicketDTO(Ticket ticket) {
        this.id = ticket.getId();
        this.ticketNumber = ticket.getTicketNumber();
        this.ticketCategoryName = ticket.getTicketCategory() != null ?
                ticket.getTicketCategory().getName() : null;
        this.ticketType = ticket.getTicketCategory() != null && ticket.getTicketCategory().getTicketType() != null ?
                ticket.getTicketCategory().getTicketType(): null;
        this.seatNumber = ticket.getSeat() != null ?
                ticket.getSeat().getSeatNumber() : null;
        this.price = ticket.getPrice();
        this.status = ticket.getStatus();
    }


}
