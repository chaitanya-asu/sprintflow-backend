package com.sprintflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.application.name:SprintFlow}")
    private String appName;

    public void sendCredentials(String toEmail, String name, String tempPassword) {
        if (mailSender == null) {
            // Log instead of crash if mail not configured
            System.out.printf("[EmailService] Credentials for %s (%s): %s%n", name, toEmail, tempPassword);
            return;
        }
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("Your " + appName + " Account Credentials");
        msg.setText(
            "Dear " + name + ",\n\n" +
            "Your " + appName + " account has been created.\n\n" +
            "Login URL : http://localhost:5173/login\n" +
            "Email     : " + toEmail + "\n" +
            "Password  : " + tempPassword + "\n\n" +
            "Please log in and change your password immediately.\n" +
            "This is a system-generated password — do not share it.\n\n" +
            "Regards,\n" + appName + " Admin Team"
        );
        mailSender.send(msg);
    }
}
