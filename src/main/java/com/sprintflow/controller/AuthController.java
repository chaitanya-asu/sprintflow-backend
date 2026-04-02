package com.sprintflow.controller;

import com.sprintflow.dto.LoginDTO;
import com.sprintflow.dto.AuthResponseDTO;
import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class AuthController {

    @Autowired private AuthService authService;

    // POST /api/auth/login  { email, password }
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> login(@RequestBody LoginDTO loginDTO) {
        AuthResponseDTO response = authService.login(loginDTO);
        return ResponseEntity.ok(ApiResponseDTO.<AuthResponseDTO>builder()
                .success(true).message("Login successful").data(response).statusCode(200).build());
    }

    // POST /api/auth/logout  (client clears token — server-side is stateless)
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<String>> logout() {
        return ResponseEntity.ok(ApiResponseDTO.<String>builder()
                .success(true).message("Logout successful").data(null).statusCode(200).build());
    }

    // GET /api/auth/me  — get current user from token
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> me(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        AuthResponseDTO info = authService.getCurrentUserInfo(token);
        return ResponseEntity.ok(ApiResponseDTO.<AuthResponseDTO>builder()
                .success(true).message("User info retrieved").data(info).statusCode(200).build());
    }

    // GET /api/auth/validate
    @GetMapping("/validate")
    public ResponseEntity<ApiResponseDTO<String>> validate(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        authService.validateToken(token);
        return ResponseEntity.ok(ApiResponseDTO.<String>builder()
                .success(true).message("Token is valid").data(null).statusCode(200).build());
    }
}
