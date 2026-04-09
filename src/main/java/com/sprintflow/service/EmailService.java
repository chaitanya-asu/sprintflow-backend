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

    /**
     * Sends an absence notification to an employee after the trainer submits attendance.
     *
     * @param toEmail        employee email
     * @param employeeName   employee full name
     * @param sprintTitle    sprint name  (e.g. "Java Sprint 1")
     * @param date           attendance date string (e.g. "2025-07-15")
     * @param timeSlot       sprint time slot (e.g. "09:00 AM – 05:00 PM")
     * @param trainerNote    optional note the trainer added for this record
     */
    public void sendAbsenceNotification(String toEmail, String employeeName,
                                        String sprintTitle, String date,
                                        String timeSlot, String trainerNote) {
        if (mailSender == null) {
            System.out.printf("[EmailService] Absence notification for %s (%s) on %s%n",
                    employeeName, toEmail, date);
            return;
        }
        String noteSection = (trainerNote != null && !trainerNote.isBlank())
                ? "\nTrainer Note : " + trainerNote + "\n"
                : "";
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("[" + appName + "] Absence Recorded — " + sprintTitle + " on " + date);
        msg.setText(
            "Dear " + employeeName + ",\n\n" +
            "This is an automated notification from " + appName + ".\n\n" +
            "You have been marked ABSENT for the following session:\n" +
            "Sprint   : " + sprintTitle + "\n" +
            "Date     : " + date + "\n" +
            "Time     : " + timeSlot + "\n" +
            noteSection + "\n" +
            "If you believe this is an error, please contact your trainer.\n\n" +
            "Regards,\n" + appName + " Team"
        );
        mailSender.send(msg);
    }
}
