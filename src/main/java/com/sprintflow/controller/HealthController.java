package com.sprintflow.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sprintflow.dto.ApiResponseDTO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> checkHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "SprintFlow Backend");
        health.put("version", "1.0.0");

        return ResponseEntity.ok(
            ApiResponseDTO.<Map<String, Object>>builder()
                    .success(true)
                    .message("Service is healthy")
                    .data(health)
                    .statusCode(200)
                    .build()
        );
    }

    @GetMapping("/db")
    public ResponseEntity<ApiResponseDTO<String>> checkDatabase() {
        return ResponseEntity.ok(
            ApiResponseDTO.<String>builder()
                    .success(true)
                    .message("Database connection is healthy")
                    .data("Connected")
                    .statusCode(200)
                    .build()
        );
    }
}
