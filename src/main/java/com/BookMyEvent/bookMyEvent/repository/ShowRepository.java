package com.BookMyEvent.bookMyEvent.repository;

import com.BookMyEvent.bookMyEvent.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show,Long> {

    @Query("SELECT s FROM Show s " +
            "JOIN s.event e " +
            "JOIN s.venue v " +
            "JOIN v.city c " +
            "WHERE e.name = :eventName AND c.location = :cityLocation")
    List<Show> findShowsByEventAndCity(@Param("eventName") String eventName,
                                       @Param("cityLocation") String cityLocation);

    @Query("SELECT s FROM Show s WHERE s.event.id = :eventId")
    List<Show> findByEventId(@Param("eventId") Long eventId);

    @Query("SELECT s FROM Show s WHERE s.startTime >= :startDate AND s.startTime < :endDate")
    List<Show> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

}