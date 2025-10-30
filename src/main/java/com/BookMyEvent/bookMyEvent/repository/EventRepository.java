package com.BookMyEvent.bookMyEvent.repository;

import com.BookMyEvent.bookMyEvent.entity.Event;
import com.BookMyEvent.bookMyEvent.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.eventType = :eventType")
    List<Event> findByEventType(@Param("eventType") EventType eventType);

    @Query("SELECT e FROM Event e WHERE e.category = :category")
    List<Event> findByCategory(@Param("category") EventCategory category);

    @Query("SELECT e FROM Event e WHERE e.city = :city AND e.eventDateTime >= :currentDate")
    List<Event> findUpcomingEventsByCity(@Param("city") String city, @Param("currentDate") LocalDateTime currentDate);
}
