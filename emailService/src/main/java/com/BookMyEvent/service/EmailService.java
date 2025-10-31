package com.BookMyEvent.service;

import com.BookMyEvent.dto.BookingDTO;
import com.BookMyEvent.util.EmailServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(to);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        javaMailSender.send(simpleMailMessage);
    }

    @KafkaListener(topics = "${email.topic}", groupId = "${email.groupId}")
    public void consumeBooking(BookingDTO bookingDTO) {
        String subject = EmailServiceConstants.emailSubject;
        String userName = bookingDTO.getUserName();
        String seatList = String.join(", ", bookingDTO.getSeatNumbers());
        String movieTitle = bookingDTO.getMovieName();
        String theaterName = bookingDTO.getTheaterName();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM, dd, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.US);
        String formattedDate = bookingDTO.getShowTime().format(dateFormatter);
        String showTime = bookingDTO.getShowTime().format(timeFormatter);

        String body = String.format(EmailServiceConstants.emailFormat,
                userName, movieTitle, theaterName, formattedDate, showTime, seatList
        );
        sendEmail(bookingDTO.getUserEmail(), subject, body);
    }

}
