package com.sprintflow.controller;

import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.entity.AuditLog;
import com.sprintflow.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit-logs")
@Tag(name = "Audit Logs", description = "System audit logging and tracking")
public class AuditLogController {

    @Autowired
    private AuditLogService auditLogService;

    @Operation(summary = "Get all audit logs (Manager only)")
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponseDTO<List<AuditLog>>> getAllLogs() {
        return ok("Audit logs retrieved", auditLogService.getAllLogs());
    }

    @Operation(summary = "Get audit logs paginated (Manager only)")
    @GetMapping("/paginated")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponseDTO<Page<AuditLog>>> getLogsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ok("Audit logs retrieved", auditLogService.getLogsPaginated(page, size));
    }

    @Operation(summary = "Get audit logs for current user")
    @GetMapping("/my-logs")
    public ResponseEntity<ApiResponseDTO<List<AuditLog>>> getMyLogs(Authentication auth) {
        String email = auth.getName();
        return ok("User audit logs retrieved", auditLogService.getLogsByUser(email));
    }

    @Operation(summary = "Get audit logs by user email (Manager only)")
    @GetMapping("/user/{email}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponseDTO<List<AuditLog>>> getLogsByUser(@PathVariable String email) {
        return ok("User audit logs retrieved", auditLogService.getLogsByUser(email));
    }

    @Operation(summary = "Get audit logs by entity (Manager only)")
    @GetMapping("/entity/{entityName}/{entityId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponseDTO<List<AuditLog>>> getLogsByEntity(
            @PathVariable String entityName,
            @PathVariable Long entityId) {
        return ok("Entity audit logs retrieved", auditLogService.getLogsByEntity(entityName, entityId));
    }

    @Operation(summary = "Get audit logs by date range (Manager only)")
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponseDTO<List<AuditLog>>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ok("Audit logs by date range retrieved", auditLogService.getLogsByDateRange(start, end));
    }

    @Operation(summary = "Get audit logs by action (Manager only)")
    @GetMapping("/action/{action}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponseDTO<List<AuditLog>>> getLogsByAction(@PathVariable String action) {
        return ok("Audit logs by action retrieved", auditLogService.getLogsByAction(action));
    }

    @Operation(summary = "Get audit log count for user")
    @GetMapping("/count/{email}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponseDTO<Map<String, Long>>> getCountByUser(@PathVariable String email) {
        long count = auditLogService.countByUser(email);
        return ok("Audit log count retrieved", Map.of("count", count));
    }

    private <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponseDTO.<T>builder()
                .success(true).message(message).data(data).statusCode(200).build());
    }
}
