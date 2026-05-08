package com.sprintflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket Sync Service
 * Broadcasts real-time updates to all connected clients
 */
@Service
public class WebSocketSyncService {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketSyncService.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Broadcast sprint update to all users
     */
    public void broadcastSprintUpdate(Long sprintId, String action, Object data) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "SPRINT_UPDATE");
            message.put("action", action); // CREATE, UPDATE, DELETE, STATUS_CHANGE
            message.put("sprintId", sprintId);
            message.put("data", data);
            message.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend("/topic/sprints", message);
            logger.debug("Broadcasted sprint update: {} - {}", action, sprintId);
        } catch (Exception e) {
            logger.error("Failed to broadcast sprint update", e);
        }
    }

    /**
     * Broadcast attendance update to all users
     */
    public void broadcastAttendanceUpdate(Long sprintId, String action, Object data) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "ATTENDANCE_UPDATE");
            message.put("action", action); // SUBMIT, UPDATE
            message.put("sprintId", sprintId);
            message.put("data", data);
            message.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend("/topic/attendance", message);
            logger.debug("Broadcasted attendance update: {} - Sprint {}", action, sprintId);
        } catch (Exception e) {
            logger.error("Failed to broadcast attendance update", e);
        }
    }

    /**
     * Broadcast employee update to all users
     */
    public void broadcastEmployeeUpdate(Long employeeId, String action, Object data) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "EMPLOYEE_UPDATE");
            message.put("action", action); // CREATE, UPDATE, DELETE
            message.put("employeeId", employeeId);
            message.put("data", data);
            message.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend("/topic/employees", message);
            logger.debug("Broadcasted employee update: {} - {}", action, employeeId);
        } catch (Exception e) {
            logger.error("Failed to broadcast employee update", e);
        }
    }

    /**
     * Broadcast user update to all users
     */
    public void broadcastUserUpdate(Long userId, String action, Object data) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "USER_UPDATE");
            message.put("action", action); // CREATE, UPDATE, DELETE, ROLE_CHANGE
            message.put("userId", userId);
            message.put("data", data);
            message.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend("/topic/users", message);
            logger.debug("Broadcasted user update: {} - {}", action, userId);
        } catch (Exception e) {
            logger.error("Failed to broadcast user update", e);
        }
    }

    /**
     * Broadcast system event to all users
     */
    public void broadcastSystemEvent(String eventType, String message, Object data) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "SYSTEM_EVENT");
            payload.put("eventType", eventType);
            payload.put("message", message);
            payload.put("data", data);
            payload.put("timestamp", System.currentTimeMillis());

            messagingTemplate.convertAndSend("/topic/system", payload);
            logger.info("Broadcasted system event: {}", eventType);
        } catch (Exception e) {
            logger.error("Failed to broadcast system event", e);
        }
    }

    /**
     * Send targeted message to specific user
     */
    public void sendToUser(String userEmail, String destination, Object message) {
        try {
            messagingTemplate.convertAndSendToUser(userEmail, destination, message);
            logger.debug("Sent message to user: {} - {}", userEmail, destination);
        } catch (Exception e) {
            logger.error("Failed to send message to user: {}", userEmail, e);
        }
    }
}
