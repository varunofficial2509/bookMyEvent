package com.BookMyEvent.util;

public class EmailServiceConstants {

    public static final String emailFormat =  """
                    🎉 Booking Confirmed! 🎉
    
                    Dear %s,
    
                    Your booking for "%s" at %s is confirmed!
    
                    Date: %s
                    Time: %s
                    Seats: %s
    
                    Enjoy the show!
    
                    ---
    
                    Thank you for choosing our service.
                    """;
    public static final String emailSubject = "Booking Confirmation";
}
