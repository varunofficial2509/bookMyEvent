package com.BookMyEvent.bookMyEvent.dto;

import lombok.*;

import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OutdoorBookingRequestDTO {
    private Long userId;
    private Long eventId;
    private Map<Long, Integer> ticketCategoryQuantities; // CategoryId -> Quantity

    public int getTotalTickets() {
        return ticketCategoryQuantities.values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }
}
