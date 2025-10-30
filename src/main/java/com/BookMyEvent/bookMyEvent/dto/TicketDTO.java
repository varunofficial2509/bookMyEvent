package com.BookMyEvent.bookMyEvent.dto;

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

}
