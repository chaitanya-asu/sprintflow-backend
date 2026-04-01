package com.sprintflow.controller;

import com.sprintflow.dto.LoginDTO;
import com.sprintflow.dto.RegisterDTO;
import com.sprintflow.dto.AuthResponseDTO;
import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> login(@RequestBody LoginDTO loginDTO) {
        AuthResponseDTO authResponse = authService.login(loginDTO);
        return ResponseEntity.ok(
            ApiResponseDTO.<AuthResponseDTO>builder()
                    .success(true)
                    .message("Login successful")
                    .data(authResponse)
                    .statusCode(200)
                    .build()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> register(@RequestBody RegisterDTO registerDTO) {
        AuthResponseDTO authResponse = authService.register(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponseDTO.<AuthResponseDTO>builder()
                    .success(true)
                    .message("Registration successful")
                    .data(authResponse)
                    .statusCode(201)
                    .build()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> getCurrentUser(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        AuthResponseDTO userInfo = authService.getCurrentUserInfo(token);
        return ResponseEntity.ok(
            ApiResponseDTO.<AuthResponseDTO>builder()
                    .success(true)
                    .message("User information retrieved")
                    .data(userInfo)
                    .statusCode(200)
                    .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<String>> logout() {
        return ResponseEntity.ok(
            ApiResponseDTO.<String>builder()
                    .success(true)
                    .message("Logout successful")
                    .data(null)
                    .statusCode(200)
                    .build()
        );
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponseDTO<String>> validateToken(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        authService.validateToken(token);
        return ResponseEntity.ok(
            ApiResponseDTO.<String>builder()
                    .success(true)
                    .message("Token is valid")
                    .data(null)
                    .statusCode(200)
                    .build()
        );
    }
}
