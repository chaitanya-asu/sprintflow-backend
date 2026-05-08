package com.sprintflow.controller;

import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.entity.User;
import com.sprintflow.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/setup")
@Tag(name = "Setup", description = "DEVELOPMENT ONLY - Disabled in production")
public class SetupController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${spring.profiles.active:production}")
    private String activeProfile;

    private void requireDevProfile() {
        if (!"dev".equalsIgnoreCase(activeProfile) && !"development".equalsIgnoreCase(activeProfile)) {
            throw new RuntimeException("Setup endpoint is disabled in production");
        }
    }

    @Operation(
        summary     = "Reset all user passwords to Admin@123",
        description = "DEVELOPMENT ONLY - Requires spring.profiles.active=dev or development"
    )
    @GetMapping("/reset-passwords")
    public ResponseEntity<ApiResponseDTO<String>> resetPasswords() {
        requireDevProfile();
        List<User> users = userRepository.findAll();
        String encoded = passwordEncoder.encode("Admin@123");
        users.forEach(u -> u.setPassword(encoded));
        userRepository.saveAll(users);
        return ResponseEntity.ok(ApiResponseDTO.<String>builder()
                .success(true)
                .message("Reset " + users.size() + " users to default password")
                .statusCode(200).build());
    }
}
