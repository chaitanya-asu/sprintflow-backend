package com.sprintflow.controller;

import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.dto.UserDTO;
import com.sprintflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class UserController {

    @Autowired private UserService userService;

    // GET /api/users          — all users
    // GET /api/users?role=hr  — filter by role (query param, matches frontend)
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<UserDTO>>> getUsers(
            @RequestParam(required = false) String role) {
        List<UserDTO> users = role != null
                ? userService.getUsersByRole(role)
                : userService.getAllUsers();
        return ok("Users retrieved successfully", users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<UserDTO>> getById(@PathVariable Long id) {
        return ok("User retrieved successfully", userService.getUserById(id));
    }

    // Manager creates trainer or HR — backend generates & emails credentials
    @PostMapping
    public ResponseEntity<ApiResponseDTO<UserDTO>> create(@RequestBody UserDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponseDTO.<UserDTO>builder()
                .success(true).message("User created successfully. Credentials sent to email.")
                .data(userService.createUser(dto)).statusCode(201).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<UserDTO>> update(
            @PathVariable Long id, @RequestBody UserDTO dto) {
        return ok("User updated successfully", userService.updateUser(id, dto));
    }

    // Soft delete (sets status = Inactive)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ok("User deactivated successfully", null);
    }

    // Resend credentials email
    @PostMapping("/{id}/resend-credentials")
    public ResponseEntity<ApiResponseDTO<String>> resendCredentials(@PathVariable Long id) {
        userService.resendCredentials(id);
        return ok("Credentials resent successfully", null);
    }

    // ── Helpers ──────────────────────────────────────────────

    private <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponseDTO.<T>builder()
                .success(true).message(message).data(data).statusCode(200).build());
    }
}
