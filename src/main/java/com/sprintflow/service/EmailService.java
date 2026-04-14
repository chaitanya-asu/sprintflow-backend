package com.sprintflow.service;

import com.sprintflow.entity.User;
import com.sprintflow.exception.ResourceNotFoundException;
import com.sprintflow.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

@Service
public class EmailService {

    @Autowired
    private UserRepository userRepository;

    @Value("${spring.application.name:SprintFlow}")
    private String appName;

    // 32-char key padded/truncated for AES-256
    @Value("${app.mail.encryption-key:SprintFlow#MailKey@2024!}")
    private String encryptionKey;

    // ── Encryption helpers ────────────────────────────────────

    private SecretKeySpec aesKey() {
        byte[] raw = encryptionKey.getBytes(StandardCharsets.UTF_8);
        byte[] key = new byte[32];
        System.arraycopy(raw, 0, key, 0, Math.min(raw.length, 32));
        return new SecretKeySpec(key, "AES");
    }

    public String encrypt(String plain) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, aesKey());
            return Base64.getEncoder().encodeToString(c.doFinal(plain.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encrypted) {
        try {
            Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, aesKey());
            return new String(c.doFinal(Base64.getDecoder().decode(encrypted)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    // ── Build sender from manager's stored credentials ────────

    /**
     * Builds a JavaMailSender on-the-fly using the MANAGER's smtp_email / smtp_password
     * stored in the DB. Supports Gmail, Outlook, Yahoo — detected from email domain.
     */
    private JavaMailSenderImpl buildSender(String smtpEmail, String encryptedPassword) {
        String password = decrypt(encryptedPassword);
        String host     = resolveSmtpHost(smtpEmail);

        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(587);
        sender.setUsername(smtpEmail);
        sender.setPassword(password);
        sender.setDefaultEncoding("UTF-8");

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth",               "true");
        props.put("mail.smtp.starttls.enable",     "true");
        props.put("mail.smtp.starttls.required",   "true");  // reject if TLS unavailable
        props.put("mail.smtp.ssl.trust",           host);
        props.put("mail.smtp.connectiontimeout",   "5000");
        props.put("mail.smtp.timeout",             "5000");
        props.put("mail.smtp.writetimeout",        "5000");
        return sender;
    }

    private String resolveSmtpHost(String email) {
        String domain = email.toLowerCase().replaceAll(".*@", "");
        if (domain.contains("gmail"))       return "smtp.gmail.com";
        if (domain.contains("outlook")
         || domain.contains("hotmail")
         || domain.contains("office365"))   return "smtp.office365.com";
        if (domain.contains("yahoo"))       return "smtp.mail.yahoo.com";
        // Default: try smtp.<domain>
        return "smtp." + domain;
    }

    /**
     * Finds the first active MANAGER with smtp credentials configured.
     * Throws if none found — manager must configure mail settings first.
     */
    private JavaMailSenderImpl resolveManagerSender() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() != null
                        && u.getRole().name().equals("MANAGER")
                        && u.getSmtpEmail() != null
                        && u.getSmtpPassword() != null
                        && "Active".equalsIgnoreCase(u.getStatus()))
                .findFirst()
                .map(u -> buildSender(u.getSmtpEmail(), u.getSmtpPassword()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No mail configuration found. A Manager must configure SMTP credentials in Profile → Mail Settings."));
    }

    // ── Public send methods ───────────────────────────────────

    @Async
    public void sendCredentials(String toEmail, String name, String tempPassword) {
        JavaMailSenderImpl sender;
        try {
            sender = resolveManagerSender();
        } catch (ResourceNotFoundException e) {
            System.out.printf("[EmailService] No SMTP config — credentials for %s (%s): %s%n",
                    name, toEmail, tempPassword);
            return;
        }

        String subject = "Your " + appName + " Account Credentials";
        String html = "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;border:1px solid #e5e7eb;border-radius:12px;overflow:hidden'>"
            + "<div style='background:linear-gradient(135deg,#1a1a2e,#f97316);padding:28px 32px'>"
            + "<h2 style='color:#fff;margin:0;font-size:20px'>" + appName + " — Account Created</h2></div>"
            + "<div style='padding:28px 32px;background:#fff'>"
            + "<p style='color:#374151;font-size:15px'>Dear <strong>" + name + "</strong>,</p>"
            + "<p style='color:#374151;font-size:14px'>Your <strong>" + appName + "</strong> account has been created. Use the credentials below to log in.</p>"
            + "<div style='background:#f9fafb;border:1px solid #e5e7eb;border-radius:8px;padding:16px 20px;margin:20px 0'>"
            + "<p style='margin:4px 0;font-size:13px;color:#6b7280'>Login URL</p>"
            + "<p style='margin:0 0 12px;font-size:14px;color:#111827;font-weight:600'>http://localhost:5173/login</p>"
            + "<p style='margin:4px 0;font-size:13px;color:#6b7280'>Email</p>"
            + "<p style='margin:0 0 12px;font-size:14px;color:#111827;font-weight:600'>" + toEmail + "</p>"
            + "<p style='margin:4px 0;font-size:13px;color:#6b7280'>Temporary Password</p>"
            + "<p style='margin:0;font-size:16px;color:#f97316;font-weight:700;letter-spacing:2px'>" + tempPassword + "</p>"
            + "</div>"
            + "<p style='color:#ef4444;font-size:13px;font-weight:600'>⚠ Please log in and change your password immediately.</p>"
            + "<p style='color:#9ca3af;font-size:12px;margin-top:24px'>This is a system-generated email — do not reply.</p>"
            + "</div></div>";

        sendHtml(sender, toEmail, subject, html);
    }

    @Async
    public void sendAbsenceNotification(String toEmail, String employeeName,
                                        String sprintTitle, String date,
                                        String timeSlot, String trainerNote) {
        JavaMailSenderImpl sender;
        try {
            sender = resolveManagerSender();
        } catch (ResourceNotFoundException e) {
            System.out.printf("[EmailService] No SMTP config — absence notification for %s (%s) on %s%n",
                    employeeName, toEmail, date);
            return;
        }

        String noteRow = (trainerNote != null && !trainerNote.isBlank())
                ? "<p style='margin:4px 0;font-size:13px;color:#6b7280'>Trainer Note</p>"
                  + "<p style='margin:0 0 12px;font-size:14px;color:#111827'>" + trainerNote + "</p>"
                : "";

        String subject = "[" + appName + "] Absence Recorded — " + sprintTitle + " on " + date;
        String html = "<div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;border:1px solid #e5e7eb;border-radius:12px;overflow:hidden'>"
            + "<div style='background:linear-gradient(135deg,#7f1d1d,#ef4444);padding:28px 32px'>"
            + "<h2 style='color:#fff;margin:0;font-size:20px'>" + appName + " — Absence Recorded</h2></div>"
            + "<div style='padding:28px 32px;background:#fff'>"
            + "<p style='color:#374151;font-size:15px'>Dear <strong>" + employeeName + "</strong>,</p>"
            + "<p style='color:#374151;font-size:14px'>You have been marked <strong style='color:#ef4444'>ABSENT</strong> for the following session:</p>"
            + "<div style='background:#f9fafb;border:1px solid #e5e7eb;border-radius:8px;padding:16px 20px;margin:20px 0'>"
            + "<p style='margin:4px 0;font-size:13px;color:#6b7280'>Sprint</p>"
            + "<p style='margin:0 0 12px;font-size:14px;color:#111827;font-weight:600'>" + sprintTitle + "</p>"
            + "<p style='margin:4px 0;font-size:13px;color:#6b7280'>Date</p>"
            + "<p style='margin:0 0 12px;font-size:14px;color:#111827;font-weight:600'>" + date + "</p>"
            + "<p style='margin:4px 0;font-size:13px;color:#6b7280'>Time</p>"
            + "<p style='margin:0 0 12px;font-size:14px;color:#111827;font-weight:600'>" + timeSlot + "</p>"
            + noteRow
            + "</div>"
            + "<p style='color:#374151;font-size:13px'>If you believe this is an error, please contact your trainer.</p>"
            + "<p style='color:#9ca3af;font-size:12px;margin-top:24px'>This is a system-generated email — do not reply.</p>"
            + "</div></div>";

        sendHtml(sender, toEmail, subject, html);
    }

    // ── Internal helper ───────────────────────────────────────

    private void sendHtml(JavaMailSenderImpl sender, String to, String subject, String html) {
        try {
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, false, "UTF-8");
            helper.setFrom(sender.getUsername());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);  // true = HTML
            sender.send(msg);
        } catch (Exception e) {
            System.err.printf("[EmailService] Failed to send to %s: %s%n", to, e.getMessage());
        }
    }
}
