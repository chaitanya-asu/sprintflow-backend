package com.sprintflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Email Service
 * Handles all email notifications including password reset, attendance alerts, etc.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${spring.mail.username:noreply@sprintflow.com}")
    private String fromEmail;

    /**
     * Send password reset email
     */
    @Async
    public void sendPasswordResetEmail(String toEmail, String userName, String resetToken) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
            
            String subject = "SprintFlow - Password Reset Request";
            String htmlContent = buildPasswordResetEmail(userName, resetLink);
            
            sendHtmlEmail(toEmail, subject, htmlContent);
            logger.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send password reset email to: {}", toEmail, e);
        }
    }

    /**
     * Send welcome email to new user
     */
    @Async
    public void sendWelcomeEmail(String toEmail, String userName, String temporaryPassword) {
        try {
            String loginLink = frontendUrl + "/login";
            
            String subject = "Welcome to SprintFlow!";
            String htmlContent = buildWelcomeEmail(userName, temporaryPassword, loginLink);
            
            sendHtmlEmail(toEmail, subject, htmlContent);
            logger.info("Welcome email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send welcome email to: {}", toEmail, e);
        }
    }

    /**
     * Send absence notification email
     */
    @Async
    public void sendAbsenceNotification(String toEmail, String employeeName, String sprintTitle, 
                                       LocalDateTime date, String managerEmail) {
        try {
            String subject = "SprintFlow - Absence Notification";
            String htmlContent = buildAbsenceEmail(employeeName, sprintTitle, date, managerEmail);
            
            sendHtmlEmail(toEmail, subject, htmlContent);
            logger.info("Absence notification sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send absence notification to: {}", toEmail, e);
        }
    }

    /**
     * Send sprint assignment notification
     */
    @Async
    public void sendSprintAssignmentEmail(String toEmail, String trainerName, String sprintTitle,
                                         LocalDateTime startDate, LocalDateTime endDate, String room) {
        try {
            String subject = "SprintFlow - New Sprint Assignment";
            String htmlContent = buildSprintAssignmentEmail(trainerName, sprintTitle, startDate, endDate, room);
            
            sendHtmlEmail(toEmail, subject, htmlContent);
            logger.info("Sprint assignment email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send sprint assignment email to: {}", toEmail, e);
        }
    }

    /**
     * Send sprint update notification
     */
    @Async
    public void sendSprintUpdateEmail(String toEmail, String sprintTitle, String updateDetails) {
        try {
            String subject = "SprintFlow - Sprint Update";
            String htmlContent = buildSprintUpdateEmail(sprintTitle, updateDetails);
            
            sendHtmlEmail(toEmail, subject, htmlContent);
            logger.info("Sprint update email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send sprint update email to: {}", toEmail, e);
        }
    }

    /**
     * Send task assignment notification
     */
    @Async
    public void sendTaskAssignmentEmail(String toEmail, String assigneeName, String taskTitle,
                                       String priority, LocalDateTime dueDate) {
        try {
            String subject = "SprintFlow - New Task Assignment";
            String htmlContent = buildTaskAssignmentEmail(assigneeName, taskTitle, priority, dueDate);
            
            sendHtmlEmail(toEmail, subject, htmlContent);
            logger.info("Task assignment email sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send task assignment email to: {}", toEmail, e);
        }
    }

    /**
     * Send HTML email
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        if (mailSender == null) {
            logger.warn("Mail sender not configured. Email not sent to: {}", to);
            return;
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
    }

    /**
     * Send simple text email
     */
    private void sendSimpleEmail(String to, String subject, String text) {
        if (mailSender == null) {
            logger.warn("Mail sender not configured. Email not sent to: {}", to);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        
        mailSender.send(message);
    }

    // ── Email Templates ──────────────────────────────────────────────────────

    private String buildPasswordResetEmail(String userName, String resetLink) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #0d4f4a 0%%, #14b8a6 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .button { display: inline-block; padding: 12px 30px; background: #14b8a6; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Password Reset Request</h1>
                    </div>
                    <div class="content">
                        <p>Hi %s,</p>
                        <p>We received a request to reset your password for your SprintFlow account.</p>
                        <p>Click the button below to reset your password:</p>
                        <a href="%s" class="button">Reset Password</a>
                        <p>This link will expire in 1 hour.</p>
                        <p>If you didn't request a password reset, please ignore this email or contact support if you have concerns.</p>
                        <p>Best regards,<br>SprintFlow Team</p>
                    </div>
                    <div class="footer">
                        <p>This is an automated email. Please do not reply.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, resetLink);
    }

    private String buildWelcomeEmail(String userName, String temporaryPassword, String loginLink) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #0d4f4a 0%%, #14b8a6 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .credentials { background: white; padding: 15px; border-left: 4px solid #14b8a6; margin: 20px 0; }
                    .button { display: inline-block; padding: 12px 30px; background: #14b8a6; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Welcome to SprintFlow!</h1>
                    </div>
                    <div class="content">
                        <p>Hi %s,</p>
                        <p>Your SprintFlow account has been created successfully!</p>
                        <div class="credentials">
                            <p><strong>Temporary Password:</strong> %s</p>
                            <p style="color: #d97706; font-size: 14px;">⚠️ Please change your password after first login.</p>
                        </div>
                        <a href="%s" class="button">Login Now</a>
                        <p>If you have any questions, please contact your administrator.</p>
                        <p>Best regards,<br>SprintFlow Team</p>
                    </div>
                    <div class="footer">
                        <p>This is an automated email. Please do not reply.</p>
                    </div>
                </div>
            </body>
            </html>
            """, userName, temporaryPassword, loginLink);
    }

    private String buildAbsenceEmail(String employeeName, String sprintTitle, LocalDateTime date, String managerEmail) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #dc2626; color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .alert { background: #fee2e2; border-left: 4px solid #dc2626; padding: 15px; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Absence Notification</h1>
                    </div>
                    <div class="content">
                        <p>Hi %s,</p>
                        <div class="alert">
                            <p><strong>You were marked absent for:</strong></p>
                            <p>Sprint: %s</p>
                            <p>Date: %s</p>
                        </div>
                        <p>If this is incorrect, please contact your trainer or manager immediately.</p>
                        <p>Manager: %s</p>
                        <p>Best regards,<br>SprintFlow Team</p>
                    </div>
                    <div class="footer">
                        <p>This is an automated email. Please do not reply.</p>
                    </div>
                </div>
            </body>
            </html>
            """, employeeName, sprintTitle, date.format(DATE_FORMATTER), managerEmail);
    }

    private String buildSprintAssignmentEmail(String trainerName, String sprintTitle, 
                                             LocalDateTime startDate, LocalDateTime endDate, String room) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #0d4f4a 0%%, #14b8a6 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .details { background: white; padding: 15px; border-left: 4px solid #14b8a6; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>New Sprint Assignment</h1>
                    </div>
                    <div class="content">
                        <p>Hi %s,</p>
                        <p>You have been assigned as a trainer for a new sprint:</p>
                        <div class="details">
                            <p><strong>Sprint:</strong> %s</p>
                            <p><strong>Start Date:</strong> %s</p>
                            <p><strong>End Date:</strong> %s</p>
                            <p><strong>Room:</strong> %s</p>
                        </div>
                        <p>Please log in to SprintFlow to view more details and manage attendance.</p>
                        <p>Best regards,<br>SprintFlow Team</p>
                    </div>
                    <div class="footer">
                        <p>This is an automated email. Please do not reply.</p>
                    </div>
                </div>
            </body>
            </html>
            """, trainerName, sprintTitle, startDate.format(DATE_FORMATTER), 
                 endDate.format(DATE_FORMATTER), room);
    }

    private String buildSprintUpdateEmail(String sprintTitle, String updateDetails) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #f59e0b; color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .update { background: white; padding: 15px; border-left: 4px solid #f59e0b; margin: 20px 0; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>Sprint Update</h1>
                    </div>
                    <div class="content">
                        <p>The sprint "%s" has been updated.</p>
                        <div class="update">
                            <p><strong>Update Details:</strong></p>
                            <p>%s</p>
                        </div>
                        <p>Please log in to SprintFlow to view the complete details.</p>
                        <p>Best regards,<br>SprintFlow Team</p>
                    </div>
                    <div class="footer">
                        <p>This is an automated email. Please do not reply.</p>
                    </div>
                </div>
            </body>
            </html>
            """, sprintTitle, updateDetails);
    }

    private String buildTaskAssignmentEmail(String assigneeName, String taskTitle, 
                                           String priority, LocalDateTime dueDate) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #0d4f4a 0%%, #14b8a6 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }
                    .content { background: #f9f9f9; padding: 30px; border-radius: 0 0 10px 10px; }
                    .task { background: white; padding: 15px; border-left: 4px solid #14b8a6; margin: 20px 0; }
                    .priority { display: inline-block; padding: 5px 10px; border-radius: 3px; font-weight: bold; }
                    .high { background: #fee2e2; color: #dc2626; }
                    .medium { background: #fef3c7; color: #d97706; }
                    .low { background: #dbeafe; color: #2563eb; }
                    .footer { text-align: center; margin-top: 20px; color: #666; font-size: 12px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>New Task Assignment</h1>
                    </div>
                    <div class="content">
                        <p>Hi %s,</p>
                        <p>A new task has been assigned to you:</p>
                        <div class="task">
                            <p><strong>Task:</strong> %s</p>
                            <p><strong>Priority:</strong> <span class="priority %s">%s</span></p>
                            <p><strong>Due Date:</strong> %s</p>
                        </div>
                        <p>Please log in to SprintFlow to view details and update progress.</p>
                        <p>Best regards,<br>SprintFlow Team</p>
                    </div>
                    <div class="footer">
                        <p>This is an automated email. Please do not reply.</p>
                    </div>
                </div>
            </body>
            </html>
            """, assigneeName, taskTitle, priority.toLowerCase(), priority, 
                 dueDate.format(DATE_FORMATTER));
    }

    /**
     * Send login credentials to a new user (async).
     * Called by UserService when creating or reactivating a user.
     */
    @Async
    public void sendCredentials(String toEmail, String userName, String temporaryPassword) {
        sendWelcomeEmail(toEmail, userName, temporaryPassword);
    }

    /**
     * Send login credentials synchronously (used for test-email feature).
     * Throws on SMTP failure so the caller can surface the error.
     */
    public void sendCredentialsSync(String toEmail, String userName, String temporaryPassword) {
        try {
            String loginLink = frontendUrl + "/login";
            String subject = "SprintFlow - Your Login Credentials";
            String htmlContent = buildWelcomeEmail(userName, temporaryPassword, loginLink);
            sendHtmlEmail(toEmail, subject, htmlContent);
            logger.info("Credentials email (sync) sent to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send credentials email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
}
