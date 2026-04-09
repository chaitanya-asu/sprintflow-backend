package com.sprintflow.controller;

import com.sprintflow.dto.LoginDTO;
import com.sprintflow.dto.AuthResponseDTO;
import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.dto.UserDTO;
import com.sprintflow.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
@Tag(name = "Authentication")
public class AuthController {

    @Autowired private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> login(@RequestBody LoginDTO loginDTO) {
        return ok("Login successful", authService.login(loginDTO));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<String>> logout() {
        return ok("Logout successful", null);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> refresh(
            @RequestHeader("Authorization") String authHeader) {
        return ok("Token refreshed", authService.refresh(authHeader.replace("Bearer ", "")));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> me(
            @RequestHeader("Authorization") String authHeader) {
        return ok("User info retrieved", authService.getCurrentUserInfo(authHeader.replace("Bearer ", "")));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponseDTO<String>> validate(
            @RequestHeader("Authorization") String authHeader) {
        authService.validateToken(authHeader.replace("Bearer ", ""));
        return ok("Token is valid", null);
    }

    // ── Profile endpoints (self-service for any role) ─────────

    @Operation(summary = "Get own profile")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponseDTO<UserDTO>> getProfile(Principal principal) {
        return ok("Profile retrieved", authService.getProfile(principal.getName()));
    }

    @Operation(summary = "Update own profile — name, phone, department, email")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponseDTO<UserDTO>> updateProfile(
            @RequestBody UserDTO dto, Principal principal) {
        return ok("Profile updated", authService.updateProfile(principal.getName(), dto));
    }

    @Operation(summary = "Change own password")
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponseDTO<String>> changePassword(
            @RequestBody Map<String, String> body, Principal principal) {
        authService.changePassword(principal.getName(), body.get("oldPassword"), body.get("newPassword"));
        return ok("Password changed successfully", null);
    }

    // ── Helper ────────────────────────────────────────────────

    private <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponseDTO.<T>builder()
                .success(true).message(message).data(data).statusCode(200).build());
    }
}
