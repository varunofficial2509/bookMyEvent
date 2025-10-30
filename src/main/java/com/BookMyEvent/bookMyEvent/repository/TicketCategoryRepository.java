package com.BookMyEvent.bookMyEvent.repository;

import com.BookMyEvent.bookMyEvent.entity.Event;
import com.BookMyEvent.bookMyEvent.entity.TicketCategory;
import com.BookMyEvent.bookMyEvent.model.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketCategoryRepository extends JpaRepository<TicketCategory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT tc FROM TicketCategory tc WHERE tc.id IN :categoryIds")
    List<TicketCategory> findByIdInWithLock(@Param("categoryIds") List<Long> categoryIds);

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.event.id = :eventId")
    List<TicketCategory> findByEventId(@Param("eventId") Long eventId);

    @Query("SELECT tc FROM TicketCategory tc WHERE tc.event.id = :eventId AND tc.availableQuantity > 0")
    List<TicketCategory> findAvailableByEventId(@Param("eventId") Long eventId);
}
