-- ── 10. MASTER_DATA ─────────────────────────────────────────────
-- Generic table for dropdown options (Technology, Subjects, etc.)
CREATE TABLE IF NOT EXISTS master_data (
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  category     VARCHAR(50)  NOT NULL, -- TECHNOLOGY, SUBJECT, COMM_TYPE, ROOM_STATUS
  value        VARCHAR(100) NOT NULL,
  parent_value VARCHAR(100),           -- e.g. "Java" for subject "Spring Boot"
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (id),
  UNIQUE KEY uq_master_data (category, value, parent_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Seed Master Data
INSERT IGNORE INTO master_data (category, value, parent_value) VALUES
-- Technologies
('TECHNOLOGY', 'Java', NULL),
('TECHNOLOGY', 'Python', NULL),
('TECHNOLOGY', 'Devops', NULL),
('TECHNOLOGY', 'DotNet', NULL),
('TECHNOLOGY', 'SalesForce', NULL),

-- Subjects (Java)
('SUBJECT', 'Spring Boot', 'Java'),
('SUBJECT', 'Hibernate', 'Java'),
('SUBJECT', 'JPA', 'Java'),
('SUBJECT', 'Microservices', 'Java'),

-- Subjects (Python)
('SUBJECT', 'Django', 'Python'),
('SUBJECT', 'Flask', 'Python'),
('SUBJECT', 'FastAPI', 'Python'),
('SUBJECT', 'Data Science', 'Python'),

-- Subjects (Devops)
('SUBJECT', 'Docker', 'Devops'),
('SUBJECT', 'Kubernetes', 'Devops'),
('SUBJECT', 'Jenkins', 'Devops'),
('SUBJECT', 'AWS', 'Devops'),

-- Subjects (DotNet)
('SUBJECT', 'ASP.NET', 'DotNet'),
('SUBJECT', '.NET Core', 'DotNet'),
('SUBJECT', 'Entity Framework', 'DotNet'),
('SUBJECT', 'MVC', 'DotNet'),

-- Subjects (SalesForce)
('SUBJECT', 'Apex', 'SalesForce'),
('SUBJECT', 'Lightning', 'SalesForce'),
('SUBJECT', 'SOQL', 'SalesForce'),
('SUBJECT', 'Integration', 'SalesForce'),

-- Communication Types
('COMM_TYPE', 'Soft Skills', NULL),
('COMM_TYPE', 'Leadership', NULL),
('COMM_TYPE', 'Presentation', NULL),
('COMM_TYPE', 'Business Etiquette', NULL);
