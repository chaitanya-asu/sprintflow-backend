package com.sprintflow.controller;

import com.sprintflow.dto.SprintDTO;
import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.security.JwtTokenProvider;
import com.sprintflow.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sprints")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class SprintController {

    @Autowired private SprintService sprintService;
    @Autowired private JwtTokenProvider jwtTokenProvider;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<SprintDTO>>> getAllSprints(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String technology) {
        List<SprintDTO> sprints = status != null
                ? sprintService.getSprintsByStatus(status)
                : sprintService.getAllSprints();
        return ok("Sprints retrieved successfully", sprints);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<SprintDTO>> getById(@PathVariable Long id) {
        return ok("Sprint retrieved successfully", sprintService.getSprintById(id));
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<ApiResponseDTO<List<SprintDTO>>> getByTrainer(@PathVariable Long trainerId) {
        return ok("Sprints retrieved successfully", sprintService.getSprintsForTrainer(trainerId));
    }

    // HR creates sprint — createdBy extracted from JWT
    @PostMapping
    public ResponseEntity<ApiResponseDTO<SprintDTO>> createSprint(
            @RequestBody SprintDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = getUserIdFromHeader(authHeader);
        SprintDTO created = sprintService.createSprint(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponseDTO.<SprintDTO>builder()
                .success(true).message("Sprint created successfully")
                .data(created).statusCode(201).build());
    }

    // Full update (HR + TRAINER)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<SprintDTO>> updateSprint(
            @PathVariable Long id, @RequestBody SprintDTO dto) {
        return ok("Sprint updated successfully", sprintService.updateSprint(id, dto));
    }

    // Status-only update (HR + TRAINER)
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponseDTO<SprintDTO>> updateStatus(
            @PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ok("Status updated successfully", sprintService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> deleteSprint(@PathVariable Long id) {
        sprintService.deleteSprint(id);
        return ok("Sprint deleted successfully", null);
    }

    // ── Helpers ──────────────────────────────────────────────

    private Long getUserIdFromHeader(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return jwtTokenProvider.getUserIdFromToken(token);
    }

    private <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponseDTO.<T>builder()
                .success(true).message(message).data(data).statusCode(200).build());
    }
}
