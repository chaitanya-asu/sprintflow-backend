-- SprintFlow — Auto-initialization script
-- Runs automatically when MySQL container starts for the first time
-- (only when the data volume is empty — fresh install)
-- MySQL executes all .sql files in /docker-entrypoint-initdb.d/ alphabetically.
-- The database 'sprintflow_db' is already created by MYSQL_DATABASE env var.

USE sprintflow_db;

-- ── 1. USERS ─────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
  id                 BIGINT          NOT NULL AUTO_INCREMENT,
  name               VARCHAR(100)    NOT NULL,
  email              VARCHAR(150)    NOT NULL,
  password           VARCHAR(255)    NOT NULL,
  role               ENUM('MANAGER','HR','TRAINER') NOT NULL,
  phone              VARCHAR(15),
  department         VARCHAR(100),
  trainer_role       VARCHAR(50),
  status             VARCHAR(10)     NOT NULL DEFAULT 'Active',
  joined_date        DATE,
  temp_password      VARCHAR(255),
  password_changed   TINYINT(1)      NOT NULL DEFAULT 0,
  smtp_email         VARCHAR(150),
  smtp_password      VARCHAR(255),
  reset_token        VARCHAR(255),
  reset_token_expiry DATETIME,
  created_at         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 2. EMPLOYEES ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS employees (
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  emp_id       VARCHAR(20)  NOT NULL,
  name         VARCHAR(100) NOT NULL,
  email        VARCHAR(150),
  phone        VARCHAR(15),
  technology   VARCHAR(20)  NOT NULL,
  cohort       VARCHAR(50)  NOT NULL,
  department   VARCHAR(100),
  status       VARCHAR(10)  NOT NULL DEFAULT 'Active',
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_employees_emp_id (emp_id),
  UNIQUE KEY uq_employees_email  (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 3. SPRINTS ───────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS sprints (
  id                BIGINT       NOT NULL AUTO_INCREMENT,
  title             VARCHAR(150) NOT NULL,
  technology        VARCHAR(20),
  cohort            VARCHAR(50),
  cohorts_json      TEXT,
  trainer_id        BIGINT,
  created_by        BIGINT,
  room              VARCHAR(100),
  start_date        DATE         NOT NULL,
  end_date          DATE         NOT NULL,
  sprint_start_time VARCHAR(20),
  sprint_end_time   VARCHAR(20),
  status            VARCHAR(15)  NOT NULL DEFAULT 'Scheduled',
  instructions      TEXT,
  created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_sprints_trainer    FOREIGN KEY (trainer_id) REFERENCES users (id) ON DELETE SET NULL,
  CONSTRAINT fk_sprints_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE SET NULL,
  INDEX idx_sprints_trainer (trainer_id),
  INDEX idx_sprints_status  (status),
  INDEX idx_sprints_dates   (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 4. SPRINT_EMPLOYEES ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS sprint_employees (
  id           BIGINT      NOT NULL AUTO_INCREMENT,
  sprint_id    BIGINT      NOT NULL,
  employee_id  BIGINT      NOT NULL,
  status       VARCHAR(15) NOT NULL DEFAULT 'ENROLLED',
  enrolled_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_sprint_employee (sprint_id, employee_id),
  CONSTRAINT fk_se_sprint   FOREIGN KEY (sprint_id)   REFERENCES sprints   (id) ON DELETE CASCADE,
  CONSTRAINT fk_se_employee FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE CASCADE,
  INDEX idx_se_sprint   (sprint_id),
  INDEX idx_se_employee (employee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 5. ATTENDANCE ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS attendance (
  id               BIGINT      NOT NULL AUTO_INCREMENT,
  sprint_id        BIGINT      NOT NULL,
  employee_id      BIGINT      NOT NULL,
  attendance_date  DATE        NOT NULL,
  status           VARCHAR(10) NOT NULL,
  check_in_time    VARCHAR(20),
  notes            VARCHAR(255),
  submitted        TINYINT(1)  NOT NULL DEFAULT 0,
  marked_by        BIGINT,
  created_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at       DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uq_attendance (sprint_id, employee_id, attendance_date),
  CONSTRAINT fk_att_sprint   FOREIGN KEY (sprint_id)   REFERENCES sprints   (id) ON DELETE CASCADE,
  CONSTRAINT fk_att_employee FOREIGN KEY (employee_id) REFERENCES employees (id) ON DELETE CASCADE,
  CONSTRAINT fk_att_marker   FOREIGN KEY (marked_by)   REFERENCES users     (id) ON DELETE SET NULL,
  INDEX idx_att_sprint   (sprint_id),
  INDEX idx_att_employee (employee_id),
  INDEX idx_att_date     (attendance_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 6. CHAT_MESSAGES ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS chat_messages (
  id               BIGINT       NOT NULL AUTO_INCREMENT,
  sender_email     VARCHAR(150) NOT NULL,
  sender_name      VARCHAR(100) NOT NULL,
  sender_role      VARCHAR(20)  NOT NULL,
  recipient_email  VARCHAR(150) NOT NULL,
  content          TEXT         NOT NULL,
  sent_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  delivered        TINYINT(1)   NOT NULL DEFAULT 0,
  read_at          DATETIME,
  PRIMARY KEY (id),
  INDEX idx_cm_sender    (sender_email),
  INDEX idx_cm_recipient (recipient_email),
  INDEX idx_cm_convo     (sender_email, recipient_email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ── 7. MESSAGES ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS messages (
  id               BIGINT       NOT NULL AUTO_INCREMENT,
  sender_email     VARCHAR(150) NOT NULL,
  sender_name      VARCHAR(100),
  sender_role      VARCHAR(20),
  recipient_email  VARCHAR(150) NOT NULL,
  content          TEXT         NOT NULL,
  sent_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  read_at          DATETIME,
  delivered        TINYINT(1)   NOT NULL DEFAULT 0,
  PRIMARY KEY (id),
  INDEX idx_msg_sender    (sender_email),
  INDEX idx_msg_recipient (recipient_email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
