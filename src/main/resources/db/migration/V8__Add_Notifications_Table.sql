-- Create notifications table for real-time notification system
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_email VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL DEFAULT 'INFO', -- INFO, SUCCESS, WARNING, ERROR, AUDIT
    is_read BOOLEAN DEFAULT FALSE,
    action_url VARCHAR(500),
    priority INT DEFAULT 0, -- 0=low, 1=medium, 2=high
    category VARCHAR(50), -- SPRINT, ATTENDANCE, EMPLOYEE, SYSTEM, AUDIT
    related_entity_type VARCHAR(50), -- Sprint, Employee, Attendance, User
    related_entity_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes for performance
    INDEX idx_user_email (user_email),
    INDEX idx_created_at (created_at),
    INDEX idx_is_read (is_read),
    INDEX idx_priority (priority),
    INDEX idx_category (category),
    INDEX idx_user_email_read_created (user_email, is_read, created_at),
    
    -- Foreign key constraint
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_email) REFERENCES users(email) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
