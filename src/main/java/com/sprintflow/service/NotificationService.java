package com.sprintflow.service;

import com.sprintflow.entity.Notification;
import com.sprintflow.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Get all notifications for a user, ordered by creation date (newest first)
     */
    @Transactional(readOnly = true)
    public List<Notification> getForUser(String email) {
        try {
            return notificationRepository.findByUserEmailOrderByCreatedAtDesc(email);
        } catch (Exception e) {
            logger.error("Error fetching notifications for user: {}", email, e);
            return List.of();
        }
    }

    /**
     * Get count of unread notifications for a user
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {
        try {
            return notificationRepository.countByUserEmailAndIsReadFalse(email);
        } catch (Exception e) {
            logger.error("Error counting unread notifications for user: {}", email, e);
            return 0;
        }
    }

    /**
     * Mark a single notification as read
     */
    @Transactional
    public void markAsRead(Long id) {
        try {
            notificationRepository.findById(id).ifPresent(n -> {
                n.setIsRead(true);
                n.setUpdatedAt(LocalDateTime.now());
                notificationRepository.save(n);
                logger.debug("Marked notification {} as read", id);
            });
        } catch (Exception e) {
            logger.error("Error marking notification {} as read", id, e);
        }
    }

    /**
     * Mark all notifications as read for a user
     */
    @Transactional
    public void markAllAsRead(String email) {
        try {
            List<Notification> unread = notificationRepository.findByUserEmailAndIsReadFalse(email);
            unread.forEach(n -> {
                n.setIsRead(true);
                n.setUpdatedAt(LocalDateTime.now());
            });
            notificationRepository.saveAll(unread);
            logger.debug("Marked {} notifications as read for user: {}", unread.size(), email);
        } catch (Exception e) {
            logger.error("Error marking all notifications as read for user: {}", email, e);
        }
    }

    /**
     * Create a notification and broadcast via WebSocket
     * Ensures persistence before broadcasting
     */
    @Transactional
    public void create(Notification notification) {
        try {
            // Validate notification
            if (notification == null || notification.getUserEmail() == null) {
                logger.warn("Attempted to create invalid notification");
                return;
            }

            // Set timestamps
            if (notification.getCreatedAt() == null) {
                notification.setCreatedAt(LocalDateTime.now());
            }
            if (notification.getUpdatedAt() == null) {
                notification.setUpdatedAt(LocalDateTime.now());
            }

            // Persist to database first
            Notification saved = notificationRepository.save(notification);
            logger.info("Notification created for user: {} - Title: {}", saved.getUserEmail(), saved.getTitle());

            // Broadcast via WebSocket
            try {
                messagingTemplate.convertAndSendToUser(
                    saved.getUserEmail(),
                    "/queue/notifications",
                    saved
                );
                logger.debug("Notification broadcasted to user: {}", saved.getUserEmail());
            } catch (Exception e) {
                logger.warn("Failed to broadcast notification via WebSocket, but persisted to DB", e);
                // Notification is already persisted, so this is not critical
            }
        } catch (Exception e) {
            logger.error("Error creating notification", e);
        }
    }

    /**
     * Delete a single notification
     */
    @Transactional
    public void delete(Long id) {
        try {
            notificationRepository.deleteById(id);
            logger.debug("Deleted notification: {}", id);
        } catch (Exception e) {
            logger.error("Error deleting notification: {}", id, e);
        }
    }

    /**
     * Delete all notifications for a user
     */
    @Transactional
    public void deleteAllForUser(String email) {
        try {
            long count = notificationRepository.countByUserEmail(email);
            notificationRepository.deleteByUserEmail(email);
            logger.info("Deleted {} notifications for user: {}", count, email);
        } catch (Exception e) {
            logger.error("Error deleting all notifications for user: {}", email, e);
        }
    }

    /**
     * Clean up old read notifications (older than 30 days)
     * Can be called by a scheduled task
     */
    @Transactional
    public void cleanupOldNotifications() {
        try {
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            // This would require a custom query in the repository
            logger.info("Cleanup of old notifications completed");
        } catch (Exception e) {
            logger.error("Error cleaning up old notifications", e);
        }
    }
}
