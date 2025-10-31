package com.BookMyEvent.bookMyEvent.controller;

import com.BookMyEvent.bookMyEvent.dto.SeatDTO;
import com.BookMyEvent.bookMyEvent.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SeatController {

    private final SeatService seatService;

    @Autowired
    public SeatController(SeatService seatService) {
        this.seatService= seatService;
    }

    @GetMapping("/available-seats")
    private List<SeatDTO> getAvailableSeatByShowId(@RequestParam("showId") Long showId){
        return seatService.getAvailableSeatByShowId(showId);
    }
}
