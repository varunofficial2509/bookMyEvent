package com.BookMyEvent.bookMyEvent.service;


import com.BookMyEvent.bookMyEvent.dto.BookingDTO;
import com.BookMyEvent.bookMyEvent.dto.ShowDTO;
import com.BookMyEvent.bookMyEvent.entity.Booking;
import com.BookMyEvent.bookMyEvent.entity.User;
import com.BookMyEvent.bookMyEvent.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Service
public class UserServiceImpl {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.encoder = new BCryptPasswordEncoder(12);
    }

    public User createUser(User user){
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<BookingDTO> getUserBookings(Long userId) {
        return userRepository.findUserBookingsByUserId(userId)
                .stream()
                .map(BookingDTO::new)
                .collect(Collectors.toList());
    }
}
