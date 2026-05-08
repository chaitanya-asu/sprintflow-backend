package com.sprintflow.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.dto.UserDTO;
import com.sprintflow.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Trainer and HR user management. MANAGER role only.")
public class UserController {

    @Autowired private UserService userService;

    @Operation(
        summary     = "List users",
        description = "Returns users filtered by `role`. " +
                      "By default returns **Active only** (for dropdowns). " +
                      "Pass `includeInactive=true` to include Inactive users (manager restore pages).\n\n" +
                      "| role param | Returns |\n|---|---|\n| `TRAINER` | Trainers |\n| `HR` | HR users |\n| *(omit)* | All users |"
    )
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<UserDTO>>> getUsers(
            @Parameter(description = "Filter by role: TRAINER or HR", example = "TRAINER")
            @RequestParam(required = false) String role,
            @Parameter(description = "Include inactive users (manager restore flow)", example = "false")
            @RequestParam(required = false, defaultValue = "false") boolean includeInactive) {
        List<UserDTO> users;
        if (role != null) {
            users = includeInactive
                    ? userService.getAllUsersByRole(role)
                    : userService.getUsersByRole(role);
        } else {
            users = userService.getAllUsers();
        }
        return ok("Users retrieved successfully", users);
    }

    @Operation(
        summary     = "Filter trainers by type and technology",
        description = "Returns trainers filtered by trainer type (TECHNOLOGY/COMMUNICATION) and optionally by technology. " +
                      "Used in HR Create Sprint form to show only relevant trainers."
    )
    @GetMapping("/trainers/filter")
    public ResponseEntity<ApiResponseDTO<List<UserDTO>>> filterTrainers(
            @Parameter(description = "Trainer type: TECHNOLOGY or COMMUNICATION", example = "TECHNOLOGY")
            @RequestParam(required = false) String type,
            @Parameter(description = "Technology filter (only for TECHNOLOGY type): Java, Python, Devops, DotNet, SalesForce", example = "Java")
            @RequestParam(required = false) String technology) {
        List<UserDTO> trainers = userService.filterTrainersByTypeAndTechnology(type, technology);
        return ok("Trainers filtered successfully", trainers);
    }

    @Operation(summary = "Get user by ID")
    @ApiResponse(responseCode = "404", description = "User not found")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<UserDTO>> getById(
            @Parameter(description = "User ID", example = "2") @PathVariable Long id) {
        return ok("User retrieved successfully", userService.getUserById(id));
    }

    @Operation(
        summary     = "Create user (Trainer or HR)",
        description = "Creates a new Trainer or HR user. Backend auto-generates a random password " +
                      "and emails the credentials to the user. **MANAGER only.**",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = """
                    {
                      "name": "Ravi Kumar",
                      "email": "ravi.kumar@ajacs.in",
                      "role": "TRAINER",
                      "phone": "9876543210",
                      "department": "Java",
                      "trainerRole": "Manager-Trainings",
                      "joinedDate": "2026-04-01"
                    }
                    """))
        )
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created, credentials emailed"),
        @ApiResponse(responseCode = "409", description = "Email already exists"),
        @ApiResponse(responseCode = "403", description = "MANAGER role required")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<UserDTO>> create(@RequestBody UserDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponseDTO.<UserDTO>builder()
                .success(true).message("User created successfully. Credentials sent to email.")
                .data(userService.createUser(dto)).statusCode(201).build());
    }

    @Operation(summary = "Update user", description = "Update user profile fields. **MANAGER only.**")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<UserDTO>> update(
            @PathVariable Long id, @RequestBody UserDTO dto) {
        return ok("User updated successfully", userService.updateUser(id, dto));
    }

    @Operation(
        summary     = "Deactivate user",
        description = "Soft delete — sets user status to `Inactive`. Does not remove from DB. **MANAGER only.**"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ok("User deactivated successfully", null);
    }

    @Operation(summary = "Resend credentials email",
        description = "Generates a new temporary password and emails it to the user. **MANAGER only.**")
    @PostMapping("/{id}/resend-credentials")
    public ResponseEntity<ApiResponseDTO<String>> resendCredentials(@PathVariable Long id) {
        userService.resendCredentials(id);
        return ok("Credentials resent successfully", null);
    }

    @Operation(summary = "Restore user",
        description = "Reactivates a soft-deleted (Inactive) user. **MANAGER only.**")
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponseDTO<String>> restore(@PathVariable Long id) {
        userService.restoreUser(id);
        return ok("User restored successfully", null);
    }

    private <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponseDTO.<T>builder()
                .success(true).message(message).data(data).statusCode(200).build());
    }
}
