package com.BookMyEvent.bookMyEvent.service;

import com.BookMyEvent.bookMyEvent.dto.BookingDTO;
import com.BookMyEvent.bookMyEvent.exception.BookingFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BookingCBService {

    @Autowired
    private BookingService bookingService;

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public BookingDTO bookIndoorEvent(Long userId, Long showId, List<Long> seatIds) {
        System.out.println("In retry method for user " + userId);
        return bookingService.bookIndoorEvent(userId, showId, seatIds);
    }

    @Retryable(value = ObjectOptimisticLockingFailureException.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public BookingDTO bookOutdoorEvent(Long userId, Long eventId,
                                       Map<Long, Integer> ticketCategoryQuantities) {
        System.out.println("In retry method for user " + userId);
        return bookingService.bookOutdoorEvent(userId, eventId, ticketCategoryQuantities);
    }

    @Recover
    public BookingDTO bookSeatsFallBackMethod(ObjectOptimisticLockingFailureException ex, Long userId, Long showId, List<Long> seatIds) {
        System.out.println("In fall back method");
        throw new BookingFailedException("Failed to book the seats , Please try again!");
    }

}
