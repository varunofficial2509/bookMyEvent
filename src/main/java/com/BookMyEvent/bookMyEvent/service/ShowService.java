package com.BookMyEvent.bookMyEvent.service;

import com.BookMyEvent.bookMyEvent.dto.ShowDTO;
import com.BookMyEvent.bookMyEvent.repository.ShowRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowService {

    @Autowired
    private ShowRepository showRepository;

    @Cacheable(value = "showsByEventNameAndLocation", key = "#eventName + ':' + #cityLocation")
    public List<ShowDTO> findAllEventShows(String eventName, String cityLocation) {
        return showRepository.findShowsByEventAndCity(eventName,cityLocation).stream().map(ShowDTO::new).collect(Collectors.toList());
    }
}
