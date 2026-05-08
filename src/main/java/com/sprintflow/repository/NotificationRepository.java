package com.sprintflow.repository;

import com.sprintflow.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserEmailOrderByCreatedAtDesc(String userEmail);
    List<Notification> findByUserEmailAndIsReadFalse(String userEmail);
    long countByUserEmailAndIsReadFalse(String userEmail);
    long countByUserEmail(String userEmail);
    
    @Transactional
    void deleteByUserEmail(String userEmail);
}
