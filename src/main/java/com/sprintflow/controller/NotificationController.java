package com.sprintflow.controller;

import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.entity.Notification;
import com.sprintflow.repository.NotificationRepository;
import com.sprintflow.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "User notification and audit logs")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;

    @Operation(summary = "Get all notifications for current user")
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<Notification>>> getAll(Authentication auth) {
        String email = auth.getName();
        return ok("Notifications retrieved", notificationRepository.findByUserEmailOrderByCreatedAtDesc(email));
    }

    @Operation(summary = "Get unread count")
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponseDTO<Map<String, Long>>> getUnreadCount(Authentication auth) {
        String email = auth.getName();
        long count = notificationRepository.countByUserEmailAndIsReadFalse(email);
        return ok("Unread count retrieved", Map.of("count", count));
    }

    @Operation(summary = "Mark notification as read")
    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponseDTO<String>> markAsRead(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();
        notificationRepository.findById(id).ifPresent(n -> {
            if (n.getUserEmail() != null && n.getUserEmail().equals(email)) {
                n.setIsRead(true);
                notificationRepository.save(n);
            }
        });
        return ok("Marked as read", null);
    }

    @Operation(summary = "Mark all as read")
    @PutMapping("/mark-all-read")
    public ResponseEntity<ApiResponseDTO<String>> markAllRead(Authentication auth) {
        String email = auth.getName();
        List<Notification> unread = notificationRepository.findByUserEmailAndIsReadFalse(email);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
        return ok("All marked as read", null);
    }

    @Operation(summary = "Delete notification")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();
        notificationRepository.findById(id).ifPresent(n -> {
            if (n.getUserEmail() != null && n.getUserEmail().equals(email)) {
                notificationRepository.delete(n);
            }
        });
        return ok("Deleted", null);
    }

    @Operation(summary = "Delete all notifications for current user")
    @DeleteMapping("/all")
    public ResponseEntity<ApiResponseDTO<String>> deleteAll(Authentication auth) {
        String email = auth.getName();
        notificationRepository.deleteByUserEmail(email);
        return ok("All deleted", null);
    }

    @Operation(summary = "Send test notification (for testing WebSocket)")
    @PostMapping("/test")
    public ResponseEntity<ApiResponseDTO<String>> sendTest(Authentication auth, @RequestBody(required = false) Map<String, String> payload) {
        String email = auth.getName();
        Notification notif = new Notification();
        notif.setUserEmail(email);
        notif.setTitle(payload != null && payload.containsKey("title") ? payload.get("title") : "Test Notification");
        notif.setMessage(payload != null && payload.containsKey("message") ? payload.get("message") : "This is a test notification");
        notif.setType(payload != null && payload.containsKey("type") ? payload.get("type") : "INFO");
        notif.setIsRead(false);
        notificationService.create(notif);
        return ok("Test notification sent", null);
    }

    private <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponseDTO.<T>builder()
                .success(true).message(message).data(data).statusCode(200).build());
    }
}
