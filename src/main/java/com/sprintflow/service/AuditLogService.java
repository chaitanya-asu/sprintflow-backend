package com.sprintflow.service;

import com.sprintflow.entity.AuditLog;
import com.sprintflow.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(String action, String performedBy, String details, String entityName, Long entityId) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .action(action)
                    .performedBy(performedBy)
                    .details(details)
                    .entityName(entityName)
                    .entityId(entityId)
                    .build();
            auditLogRepository.save(auditLog);
            logger.debug("Audit log created: {} by {}", action, performedBy);
        } catch (Exception e) {
            logger.error("Failed to create audit log", e);
        }
    }

    @Transactional
    public void logWithValues(String action, String performedBy, String userRole, String ipAddress,
                              String details, String entityName, Long entityId,
                              String oldValue, String newValue) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .action(action)
                    .performedBy(performedBy)
                    .userRole(userRole)
                    .ipAddress(ipAddress)
                    .details(details)
                    .entityName(entityName)
                    .entityId(entityId)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .build();
            auditLogRepository.save(auditLog);
            logger.info("Audit log: {} - {} by {} ({})", action, entityName, performedBy, userRole);
        } catch (Exception e) {
            logger.error("Failed to create audit log with values", e);
        }
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAllLogs() {
        try {
            return auditLogRepository.findAll();
        } catch (Exception e) {
            logger.error("Failed to fetch all audit logs", e);
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> getLogsPaginated(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            return auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        } catch (Exception e) {
            logger.error("Failed to fetch paginated audit logs", e);
            return Page.empty();
        }
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByUser(String email) {
        try {
            return auditLogRepository.findByPerformedByOrderByCreatedAtDesc(email);
        } catch (Exception e) {
            logger.error("Failed to fetch audit logs for user: {}", email, e);
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByEntity(String entityName, Long entityId) {
        try {
            return auditLogRepository.findByEntityNameAndEntityIdOrderByCreatedAtDesc(entityName, entityId);
        } catch (Exception e) {
            logger.error("Failed to fetch audit logs for entity: {} {}", entityName, entityId, e);
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        try {
            return auditLogRepository.findByDateRange(start, end);
        } catch (Exception e) {
            logger.error("Failed to fetch audit logs by date range", e);
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByAction(String action) {
        try {
            return auditLogRepository.findByAction(action);
        } catch (Exception e) {
            logger.error("Failed to fetch audit logs by action: {}", action, e);
            return List.of();
        }
    }

    @Transactional(readOnly = true)
    public long countByUser(String email) {
        try {
            return auditLogRepository.countByPerformedBy(email);
        } catch (Exception e) {
            logger.error("Failed to count audit logs for user: {}", email, e);
            return 0;
        }
    }
}
