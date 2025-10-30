package com.BookMyEvent.bookMyEvent.dto;


import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BookingDTO implements Serializable {
    private String bookingId;
    private String userEmail;
    private String userName;
    private List<String> seatNumbers;
    private LocalDateTime bookingTime;
    private String eventName;
    private String venueName;
    private LocalDateTime showTime;
    private String cityName;

}
