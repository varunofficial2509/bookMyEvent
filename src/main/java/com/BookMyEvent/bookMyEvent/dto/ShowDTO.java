package com.BookMyEvent.bookMyEvent.dto;

import com.BookMyEvent.bookMyEvent.entity.Show;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ShowDTO implements Serializable {

    private Long showId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String eventName;
    private String venueName;
    private String cityName;

    public ShowDTO(Show show) {
        this.showId = show.getId();
        this.eventName = show.getEvent().getName();
        this.startTime = show.getStartTime();
        this.endTime = show.getEndTime();
        this.venueName = show.getVenue().getVenueName();
        this.cityName = show.getVenue().getCity().getCityName();
    }

}
