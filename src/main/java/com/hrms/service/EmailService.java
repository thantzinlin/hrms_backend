package com.hrms.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendVerifyEmail(String email, String token) {
        String verifyLink = "http://localhost:8080/api/auth/verify?token=" + token;
        this.sendEmail(email, "Verify Request",
                "Click the link to verify your account: " + verifyLink);
        return true;
    }

    public void sendNewPasswordEmail(String email, String newPassword) {
        String subject = "Your New Password";
        String text = "Hello, \n\nYour new password is: " + newPassword
                + "\n\nPlease use this password to log in to your account.";

        this.sendEmail(email, subject, text);
    }

    public boolean sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
            System.out.println("Email sent successfully");
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
        }
        return true;

    }
}
