package com.BookMyEvent.bookMyEvent.repository;

import com.BookMyEvent.bookMyEvent.entity.Seat;
import com.BookMyEvent.bookMyEvent.model.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.id IN :seatIds")
    List<Seat> findByIdInWithLock(@Param("seatIds") List<Long> seatIds);

    @Query("SELECT s FROM Seat s WHERE s.show.id = :showId AND s.seatStatus = :status")
    List<Seat> findByShowIdAndStatus(@Param("showId") Long showId, @Param("status") SeatStatus status);

    @Query("SELECT s FROM Seat s WHERE s.show.id = :showId AND s.ticketCategory.id = :categoryId")
    List<Seat> findByShowIdAndCategoryId(@Param("showId") Long showId, @Param("categoryId") Long categoryId);
}

