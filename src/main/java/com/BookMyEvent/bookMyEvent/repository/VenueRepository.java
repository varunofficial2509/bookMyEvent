package com.BookMyEvent.bookMyEvent.repository;

import com.BookMyEvent.bookMyEvent.entity.User;
import com.BookMyEvent.bookMyEvent.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<User, Long> {

    List<Venue> findByLocation(String location);
}
