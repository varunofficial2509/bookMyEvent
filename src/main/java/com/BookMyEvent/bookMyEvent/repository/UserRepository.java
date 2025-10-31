package com.BookMyEvent.bookMyEvent.repository;

import com.BookMyEvent.bookMyEvent.dto.BookingDTO;
import com.BookMyEvent.bookMyEvent.entity.Booking;
import com.BookMyEvent.bookMyEvent.entity.User;
import com.BookMyEvent.bookMyEvent.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId")
    List<Booking> findUserBookingsByUserId(@Param("userId")Long userId);

    public User findByUserName(String username);
}
