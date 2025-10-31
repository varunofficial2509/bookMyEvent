package com.BookMyEvent.bookMyEvent.service;

import com.BookMyEvent.bookMyEvent.dto.SeatDTO;
import com.BookMyEvent.bookMyEvent.model.SeatStatus;
import com.BookMyEvent.bookMyEvent.repository.SeatRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    @Autowired
    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Cacheable(value =  "availableSeats" , key = "#showId")
    public List<SeatDTO> getAvailableSeatByShowId(Long showId){
        return seatRepository.findByShowIdAndStatus(showId, SeatStatus.AVAILABLE).stream().map(SeatDTO::new).collect(Collectors.toList());
    }
}
