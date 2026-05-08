package com.sprintflow.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Password Policy Validator
 * Enforces strong password requirements for security
 * 
 * Requirements:
 * - Minimum 8 characters
 * - At least one uppercase letter
 * - At least one lowercase letter
 * - At least one digit
 * - At least one special character
 * - No common passwords
 */
@Component
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]");
    
    // Common passwords to reject
    private static final List<String> COMMON_PASSWORDS = List.of(
        "password", "Password1", "12345678", "qwerty", "abc123",
        "password123", "admin", "Admin123", "welcome", "Welcome1",
        "letmein", "monkey", "dragon", "master", "sunshine"
    );

    /**
     * Validate password against policy
     * @param password Password to validate
     * @return ValidationResult with success flag and error messages
     */
    public ValidationResult validate(String password) {
        List<String> errors = new ArrayList<>();
        
        if (password == null || password.isEmpty()) {
            errors.add("Password is required");
            return new ValidationResult(false, errors);
        }
        
        // Length check
        if (password.length() < MIN_LENGTH) {
            errors.add("Password must be at least " + MIN_LENGTH + " characters long");
        }
        
        if (password.length() > MAX_LENGTH) {
            errors.add("Password must not exceed " + MAX_LENGTH + " characters");
        }
        
        // Uppercase check
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one uppercase letter");
        }
        
        // Lowercase check
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one lowercase letter");
        }
        
        // Digit check
        if (!DIGIT_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one digit");
        }
        
        // Special character check
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            errors.add("Password must contain at least one special character (!@#$%^&*()_+-=[]{};\':\"\\|,.<>/?)");
        }
        
        // Common password check
        if (COMMON_PASSWORDS.contains(password.toLowerCase())) {
            errors.add("Password is too common. Please choose a more secure password");
        }
        
        // Sequential characters check
        if (hasSequentialCharacters(password)) {
            errors.add("Password should not contain sequential characters (e.g., abc, 123)");
        }
        
        // Repeated characters check
        if (hasRepeatedCharacters(password)) {
            errors.add("Password should not contain repeated characters (e.g., aaa, 111)");
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * Check if password contains sequential characters
     */
    private boolean hasSequentialCharacters(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);
            
            if (c2 == c1 + 1 && c3 == c2 + 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if password contains repeated characters
     */
    private boolean hasRepeatedCharacters(String password) {
        for (int i = 0; i < password.length() - 2; i++) {
            char c1 = password.charAt(i);
            char c2 = password.charAt(i + 1);
            char c3 = password.charAt(i + 2);
            
            if (c1 == c2 && c2 == c3) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate password strength score (0-100)
     */
    public int calculateStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }
        
        int score = 0;
        
        // Length score (max 30 points)
        score += Math.min(password.length() * 2, 30);
        
        // Character variety score (max 40 points)
        if (UPPERCASE_PATTERN.matcher(password).find()) score += 10;
        if (LOWERCASE_PATTERN.matcher(password).find()) score += 10;
        if (DIGIT_PATTERN.matcher(password).find()) score += 10;
        if (SPECIAL_CHAR_PATTERN.matcher(password).find()) score += 10;
        
        // Complexity score (max 30 points)
        long uniqueChars = password.chars().distinct().count();
        score += Math.min((int)(uniqueChars * 2), 30);
        
        // Penalties
        if (hasSequentialCharacters(password)) score -= 10;
        if (hasRepeatedCharacters(password)) score -= 10;
        if (COMMON_PASSWORDS.contains(password.toLowerCase())) score -= 20;
        
        return Math.max(0, Math.min(100, score));
    }

    /**
     * Get password strength label
     */
    public String getStrengthLabel(int score) {
        if (score < 30) return "Weak";
        if (score < 60) return "Fair";
        if (score < 80) return "Good";
        return "Strong";
    }

    /**
     * Validation result class
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorMessage() {
            return String.join("; ", errors);
        }
    }
}
