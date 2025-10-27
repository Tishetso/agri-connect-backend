package com.agriconnect.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;



    public void sendResetEmail(String toEmail, String token) {
        String subject = "AgriConnect Password Reset";
        String resetLink = "http://localhost:3000/reset-password?token=" + token;
        String body = "Click the link to reset your password:\n" + resetLink;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("tishetsomphelane@gmail.com");



        mailSender.send(message);
    }
}