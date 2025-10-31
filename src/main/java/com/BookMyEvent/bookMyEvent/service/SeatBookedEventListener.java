package com.BookMyEvent.bookMyEvent.service;

import com.BookMyEvent.bookMyEvent.dto.BookingDTO;
import com.BookMyEvent.bookMyEvent.model.ConfirmedBookingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatBookedEventListener {

    private final CacheManager cacheManager;
    private final KafkaTemplate<String, BookingDTO> kafkaTemplate;

    @Value("${kafka.topic.bookings:bookings}")
    private String bookingTopic;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSeatBookedEvent(ConfirmedBookingEvent event) {
        log.info("Processing confirmed booking event for booking: {}",
                event.getBookingDetails().getBookingReferenceId());
        invalidateSeatCache(event);

        //Sending to Kafka
        sendBookingDetailsToKafka(event.getBookingDetails());
    }

    private void invalidateSeatCache(ConfirmedBookingEvent event) {
        try {
            Long showId = event.getBookingDetails().getShowId();

            if (showId == null) {
                log.debug("No showId present, skipping cache invalidation (outdoor event)");
                return;
            }

            Cache cache = cacheManager.getCache("availableSeats");

            if (cache != null) {
                cache.evict(showId);
                log.info("Evicted cache for showId: {}", showId);
            } else {
                log.warn("Cache 'availableSeats' not found");
            }

        } catch (Exception e) {
            log.error("Failed to invalidate cache for booking: {}",
                    event.getBookingDetails().getBookingReferenceId(), e);
        }
    }

    private void sendBookingDetailsToKafka(BookingDTO bookingDetails) {
        try {
            String key = String.valueOf(bookingDetails.getBookingId());

            CompletableFuture<SendResult<String, BookingDTO>> future =
                    kafkaTemplate.send(bookingTopic, key, bookingDetails);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Successfully sent booking {} to Kafka topic {} at offset {}",
                            bookingDetails.getBookingReferenceId(),
                            bookingTopic,
                            result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to send booking {} to Kafka: {}",
                            bookingDetails.getBookingReferenceId(),
                            ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error preparing booking {} for Kafka: {}",
                    bookingDetails.getBookingReferenceId(),
                    e.getMessage(), e);
        }
    }
}