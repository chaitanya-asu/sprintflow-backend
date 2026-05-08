package com.sprintflow.util;

import org.springframework.stereotype.Component;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.util.regex.Pattern;

/**
 * Input Sanitization Utility
 * Prevents XSS, SQL injection, and other injection attacks
 * Uses OWASP Java HTML Sanitizer
 */
@Component
public class InputSanitizer {

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING
            .and(Sanitizers.LINKS)
            .and(Sanitizers.BLOCKS);

    // Patterns for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );
    
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(
        "^[A-Za-z0-9]+$"
    );
    
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "('.+--)|(--)|(;)|(\\|\\|)|(\\*)|(<)|(>)|(\\^)|(\\[)|(\\])|(\\{)|(\\})|(%7C)",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Sanitize HTML content to prevent XSS
     */
    public String sanitizeHtml(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return POLICY.sanitize(input);
    }

    /**
     * Sanitize plain text (remove HTML tags)
     */
    public String sanitizePlainText(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        // Remove all HTML tags
        String sanitized = input.replaceAll("<[^>]*>", "");
        // Remove script tags and content
        sanitized = sanitized.replaceAll("(?i)<script[^>]*>.*?</script>", "");
        // Trim whitespace
        return sanitized.trim();
    }

    /**
     * Sanitize SQL input to prevent SQL injection
     */
    public String sanitizeSql(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        // Check for SQL injection patterns
        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            throw new IllegalArgumentException("Invalid input: potential SQL injection detected");
        }
        
        // Escape single quotes
        return input.replace("'", "''");
    }

    /**
     * Sanitize email address
     */
    public String sanitizeEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        
        String sanitized = email.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        return sanitized;
    }

    /**
     * Sanitize phone number
     */
    public String sanitizePhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return phone;
        }
        
        // Remove all non-digit characters except +
        String sanitized = phone.replaceAll("[^0-9+]", "");
        
        if (!PHONE_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
        
        return sanitized;
    }

    /**
     * Sanitize alphanumeric input (e.g., employee ID)
     */
    public String sanitizeAlphanumeric(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        String sanitized = input.trim();
        
        if (!ALPHANUMERIC_PATTERN.matcher(sanitized).matches()) {
            throw new IllegalArgumentException("Input must contain only letters and numbers");
        }
        
        return sanitized;
    }

    /**
     * Sanitize file name to prevent directory traversal
     */
    public String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return fileName;
        }
        
        // Remove path separators and special characters
        String sanitized = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        // Prevent directory traversal
        sanitized = sanitized.replace("..", "_");
        sanitized = sanitized.replace("/", "_");
        sanitized = sanitized.replace("\\", "_");
        
        return sanitized;
    }

    /**
     * Sanitize URL to prevent open redirect
     */
    public String sanitizeUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        
        String sanitized = url.trim();
        
        // Only allow http and https protocols
        if (!sanitized.startsWith("http://") && !sanitized.startsWith("https://")) {
            throw new IllegalArgumentException("Invalid URL: must start with http:// or https://");
        }
        
        // Prevent javascript: and data: URLs
        if (sanitized.toLowerCase().contains("javascript:") || 
            sanitized.toLowerCase().contains("data:")) {
            throw new IllegalArgumentException("Invalid URL: javascript and data URLs are not allowed");
        }
        
        return sanitized;
    }

    /**
     * Sanitize general string input
     * Removes dangerous characters but preserves spaces and basic punctuation
     */
    public String sanitizeString(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        // Remove control characters
        String sanitized = input.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
        
        // Remove HTML tags
        sanitized = sanitized.replaceAll("<[^>]*>", "");
        
        // Trim whitespace
        return sanitized.trim();
    }

    /**
     * Validate and sanitize JSON input
     */
    public String sanitizeJson(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }
        
        // Basic JSON validation
        String sanitized = json.trim();
        
        if (!sanitized.startsWith("{") && !sanitized.startsWith("[")) {
            throw new IllegalArgumentException("Invalid JSON format");
        }
        
        // Check for script tags in JSON
        if (sanitized.toLowerCase().contains("<script")) {
            throw new IllegalArgumentException("Invalid JSON: script tags not allowed");
        }
        
        return sanitized;
    }

    /**
     * Encode output for HTML context to prevent XSS
     */
    public String encodeForHtml(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;");
    }

    /**
     * Encode output for JavaScript context
     */
    public String encodeForJavaScript(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        
        return input
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("'", "\\'")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            .replace("<", "\\x3C")
            .replace(">", "\\x3E");
    }
}
