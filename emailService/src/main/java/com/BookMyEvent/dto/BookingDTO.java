package com.BookMyEvent.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;



public class BookingDTO implements Serializable {
    private String bookingId;
    private String userEmail;
    private String userName;
    private List<String> seatNumbers;
    private LocalDateTime bookingTime;

    public BookingDTO() {
    }

    private String movieName;
    private String theaterName;
    private LocalDateTime showTime;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getTheaterName() {
        return theaterName;
    }

    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }


    public BookingDTO(String bookingId, String userEmail, String userName, String movieName, String theaterName, List<String> seatNumbers, LocalDateTime bookingTime, LocalDateTime showTime) {
        this.bookingId = bookingId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.movieName = movieName;
        this.theaterName = theaterName;
        this.seatNumbers = seatNumbers;
        this.bookingTime = bookingTime;
        this.showTime = showTime;
    }
}
