package com.BookMyEvent.bookMyEvent.dto;

import lombok.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IndoorBookingRequestDTO {
    private Long userId;
    private Long showId;
    private List<Long> seatIds;
}
