package com.BookMyEvent.bookMyEvent.controller;

import com.BookMyEvent.bookMyEvent.dto.ShowDTO;
import com.BookMyEvent.bookMyEvent.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ShowController {


    private final ShowService showService;

    @Autowired
    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping("/shows")
    public List<ShowDTO> getAllMovieShows(@RequestParam("movie") String movieTitle, @RequestParam("location") String location){
        return showService.findAllMovieShows(movieTitle,location);
    }
}