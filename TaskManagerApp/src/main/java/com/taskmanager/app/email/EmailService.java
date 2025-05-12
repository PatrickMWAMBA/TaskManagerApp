package com.taskmanager.app.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendTaskAssignmentEmail(String toEmail, String taskName) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("New Task Assigned");
        message.setText("You have been assigned a new task: " + taskName);
        message.setFrom("your-email@gmail.com");

        mailSender.send(message);
    }
}
