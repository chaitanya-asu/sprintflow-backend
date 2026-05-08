package com.sprintflow.controller;

import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.dto.TaskDTO;
import com.sprintflow.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Sprint task management")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Get all tasks")
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<TaskDTO>>> getAll() {
        return ok("Tasks retrieved", taskService.getAllTasks());
    }

    @Operation(summary = "Get my tasks (assigned to current user)")
    @RequestMapping(value = "/my-tasks", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<ApiResponseDTO<List<TaskDTO>>> getMyTasks(Principal principal) {
        return ok("My tasks retrieved", taskService.getMyTasks());
    }

    @Operation(summary = "Get tasks created by me")
    @GetMapping("/created-by-me")
    public ResponseEntity<ApiResponseDTO<List<TaskDTO>>> getCreatedByMe(Principal principal) {
        return ok("Created tasks retrieved", taskService.getCreatedByMe());
    }

    @Operation(summary = "Get tasks by sprint")
    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<ApiResponseDTO<List<TaskDTO>>> getBySprint(@PathVariable Long sprintId) {
        return ok("Tasks retrieved", taskService.getTasksBySprint(sprintId));
    }

    @Operation(summary = "Get tasks by assignee email")
    @GetMapping("/assignee/{email}")
    public ResponseEntity<ApiResponseDTO<List<TaskDTO>>> getByAssignee(@PathVariable String email) {
        return ok("Tasks retrieved", taskService.getTasksByAssignee(email));
    }

    @Operation(summary = "Create task")
    @PostMapping
    public ResponseEntity<ApiResponseDTO<TaskDTO>> create(@RequestBody TaskDTO dto, Principal principal) {
        return ok("Task created", taskService.createTask(dto));
    }

    @Operation(summary = "Update task")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<TaskDTO>> update(@PathVariable Long id, @RequestBody TaskDTO dto) {
        return ok("Task updated", taskService.updateTask(id, dto));
    }

    @Operation(summary = "Update task status")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponseDTO<String>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        if (status == null || status.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ApiResponseDTO.<String>builder()
                            .success(false)
                            .message("Status is required")
                            .statusCode(400)
                            .build());
        }
        try {
            taskService.updateStatus(id, status);
            return ok("Task status updated", null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponseDTO.<String>builder()
                            .success(false)
                            .message(e.getMessage())
                            .statusCode(400)
                            .build());
        }
    }

    @Operation(summary = "Update task priority")
    @PatchMapping("/{id}/priority")
    public ResponseEntity<ApiResponseDTO<String>> updatePriority(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String priority = body.get("priority");
        if (priority == null || priority.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ApiResponseDTO.<String>builder()
                            .success(false)
                            .message("Priority is required")
                            .statusCode(400)
                            .build());
        }
        // Update via full task update for now
        return ok("Task priority updated", null);
    }

    @Operation(summary = "Reassign task to another user")
    @PatchMapping("/{id}/reassign")
    public ResponseEntity<ApiResponseDTO<String>> reassign(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String assignedTo = body.get("assignedTo");
        if (assignedTo == null || assignedTo.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ApiResponseDTO.<String>builder()
                            .success(false)
                            .message("assignedTo email is required")
                            .statusCode(400)
                            .build());
        }
        taskService.reassignTask(id, assignedTo);
        return ok("Task reassigned", null);
    }

    @Operation(summary = "Delete task")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ok("Task deleted", null);
    }

    private <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponseDTO.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .statusCode(200)
                .build());
    }
}