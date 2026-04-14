package com.sprintflow.service;

import com.sprintflow.dto.LoginDTO;
import com.sprintflow.dto.AuthResponseDTO;
import com.sprintflow.dto.MailConfigDTO;
import com.sprintflow.entity.Role;
import com.sprintflow.entity.User;
import com.sprintflow.exception.AuthenticationException;
import com.sprintflow.exception.ResourceNotFoundException;
import com.sprintflow.repository.UserRepository;
import com.sprintflow.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private UserService      userService;
    @Autowired private EmailService     emailService;

    public AuthResponseDTO login(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword()))
            throw new AuthenticationException("Invalid email or password");

        if (!"Active".equalsIgnoreCase(user.getStatus()))
            throw new AuthenticationException("User account is inactive");

        String accessToken  = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail(), user.getRole().name());

        AuthResponseDTO.UserInfo userInfo = new AuthResponseDTO.UserInfo(
                user.getId(), user.getName(), user.getEmail(), user.getRole().name()
        );
        userInfo.setStatus(user.getStatus());

        return new AuthResponseDTO(accessToken, refreshToken, userInfo, jwtTokenProvider.getExpirationTime() / 1000);
    }

    public AuthResponseDTO refresh(String refreshToken) {
        User user = validateToken(refreshToken);
        String newAccessToken  = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail(), user.getRole().name());
        AuthResponseDTO.UserInfo userInfo = new AuthResponseDTO.UserInfo(
                user.getId(), user.getName(), user.getEmail(), user.getRole().name()
        );
        return new AuthResponseDTO(newAccessToken, newRefreshToken, userInfo, jwtTokenProvider.getExpirationTime() / 1000);
    }

    public User validateToken(String token) {
        if (!jwtTokenProvider.validateToken(token))
            throw new AuthenticationException("Invalid or expired token");

        String email = jwtTokenProvider.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public AuthResponseDTO getCurrentUserInfo(String token) {
        User user = validateToken(token);
        AuthResponseDTO.UserInfo userInfo = new AuthResponseDTO.UserInfo(
                user.getId(), user.getName(), user.getEmail(), user.getRole().name()
        );
        return new AuthResponseDTO(token, null, userInfo, 0);
    }

    public com.sprintflow.dto.UserDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userService.toDTO(user);
    }

    public com.sprintflow.dto.UserDTO updateProfile(String email, com.sprintflow.dto.UserDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userService.updateUser(user.getId(), dto);
    }

    /**
     * Updates the profile and, if the email changed, issues fresh tokens so the
     * frontend can replace the stale JWT (which still carries the old email).
     * Returns a map with keys: "user" (UserDTO) and optionally
     * "accessToken" / "refreshToken" when the email was changed.
     */
    public Map<String, Object> updateProfileWithTokens(String currentEmail, com.sprintflow.dto.UserDTO dto) {
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean emailChanging = dto.getEmail() != null
                && !dto.getEmail().isBlank()
                && !dto.getEmail().equalsIgnoreCase(currentEmail);

        com.sprintflow.dto.UserDTO updated = userService.updateUser(user.getId(), dto);

        Map<String, Object> result = new HashMap<>();
        result.put("user", updated);

        if (emailChanging) {
            // Re-fetch to get the saved email
            User refreshed = userRepository.findById(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            String newAccess  = jwtTokenProvider.generateToken(refreshed.getId(), refreshed.getEmail(), refreshed.getRole().name());
            String newRefresh = jwtTokenProvider.generateRefreshToken(refreshed.getId(), refreshed.getEmail(), refreshed.getRole().name());
            result.put("accessToken",  newAccess);
            result.put("refreshToken", newRefresh);
        }
        return result;
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        if (oldPassword == null || newPassword == null || newPassword.length() < 6)
            throw new AuthenticationException("Invalid password");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new AuthenticationException("Current password is incorrect");
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChanged(true);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        userRepository.save(user);
    }

    // ── Mail config (MANAGER only) ────────────────────────────

    /** Save encrypted SMTP credentials for the calling manager. */
    public void saveMailConfig(String managerEmail, MailConfigDTO dto) {
        if (dto.getSmtpEmail() == null || dto.getSmtpEmail().isBlank()
                || dto.getSmtpPassword() == null || dto.getSmtpPassword().isBlank())
            throw new AuthenticationException("SMTP email and password are required");

        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (manager.getRole() != Role.MANAGER)
            throw new AuthenticationException("Only managers can configure mail settings");

        manager.setSmtpEmail(dto.getSmtpEmail().trim().toLowerCase());
        manager.setSmtpPassword(emailService.encrypt(dto.getSmtpPassword()));
        manager.setUpdatedAt(java.time.LocalDateTime.now());
        userRepository.save(manager);
    }

    /** Returns whether the calling manager has SMTP credentials saved. */
    public Map<String, Object> getMailConfigStatus(String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Map<String, Object> status = new HashMap<>();
        boolean configured = manager.getSmtpEmail() != null && manager.getSmtpPassword() != null;
        status.put("configured", configured);
        status.put("smtpEmail",  configured ? manager.getSmtpEmail() : null);
        return status;
    }

    /** Sends a test email to the manager's own address to verify SMTP works. */
    public void testMailConfig(String managerEmail) {
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (manager.getSmtpEmail() == null || manager.getSmtpPassword() == null)
            throw new AuthenticationException("Mail not configured. Save SMTP credentials first.");
        // Reuse sendCredentials with a dummy test payload to the manager themselves
        emailService.sendCredentials(managerEmail, manager.getName(), "[TEST — ignore this email]");
    }
}
