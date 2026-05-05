#!/bin/bash

# ============================================================================
# SprintFlow - Automated Fix Execution Script (Linux/Mac)
# ============================================================================
# This script will:
# 1. Backup current database
# 2. Load clean seed data
# 3. Verify data
# 4. Rebuild project
# 5. Start application
# ============================================================================

set -e

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="backup_${TIMESTAMP}.sql"
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo ""
echo "============================================================================"
echo "                    SprintFlow - Fix Execution Script"
echo "============================================================================"
echo ""
echo "Project Directory: $PROJECT_DIR"
echo "Backup File: $BACKUP_FILE"
echo ""

# ============================================================================
# STEP 1: Backup Current Database
# ============================================================================
echo "[STEP 1] Backing up current database..."
echo ""

read -p "Enter MySQL username (default: root): " MYSQL_USER
MYSQL_USER=${MYSQL_USER:-root}

read -sp "Enter MySQL password: " MYSQL_PASS
echo ""

if [ -z "$MYSQL_PASS" ]; then
    echo "Backing up without password..."
    mysqldump -u "$MYSQL_USER" sprintflow_db > "$BACKUP_FILE"
else
    echo "Backing up with password..."
    mysqldump -u "$MYSQL_USER" -p"$MYSQL_PASS" sprintflow_db > "$BACKUP_FILE"
fi

if [ $? -eq 0 ]; then
    echo "✓ Backup created: $BACKUP_FILE"
else
    echo "✗ Backup failed!"
    exit 1
fi

echo ""

# ============================================================================
# STEP 2: Load Clean Seed Data
# ============================================================================
echo "[STEP 2] Loading clean seed data..."
echo ""

if [ -z "$MYSQL_PASS" ]; then
    mysql -u "$MYSQL_USER" sprintflow_db < CLEAN_SEED.sql
else
    mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" sprintflow_db < CLEAN_SEED.sql
fi

if [ $? -eq 0 ]; then
    echo "✓ Seed data loaded successfully"
else
    echo "✗ Seed data loading failed!"
    exit 1
fi

echo ""

# ============================================================================
# STEP 3: Verify Data
# ============================================================================
echo "[STEP 3] Verifying data..."
echo ""

QUERY="SELECT 'Users' AS table_name, COUNT(*) AS count FROM users UNION ALL SELECT 'Employees', COUNT(*) FROM employees UNION ALL SELECT 'Sprints', COUNT(*) FROM sprints UNION ALL SELECT 'Rooms', COUNT(*) FROM rooms;"

if [ -z "$MYSQL_PASS" ]; then
    mysql -u "$MYSQL_USER" sprintflow_db -e "$QUERY"
else
    mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" sprintflow_db -e "$QUERY"
fi

echo ""
echo "✓ Data verification complete"
echo ""

# ============================================================================
# STEP 4: Rebuild Project
# ============================================================================
echo "[STEP 4] Rebuilding project..."
echo ""

mvn clean install -DskipTests

if [ $? -eq 0 ]; then
    echo "✓ Project rebuilt successfully"
else
    echo "✗ Project rebuild failed!"
    exit 1
fi

echo ""

# ============================================================================
# STEP 5: Start Application
# ============================================================================
echo "[STEP 5] Starting application..."
echo ""

mvn spring-boot:run

echo ""
echo "============================================================================"
echo "                         ✓ EXECUTION COMPLETE"
echo "============================================================================"
echo ""
echo "Application is running. Test with:"
echo "   curl http://localhost:8080/api/employees"
echo ""
echo "Default credentials:"
echo "   Email: s.lakkampally@ajacs.in"
echo "   Password: Admin@123"
echo ""
echo "============================================================================"
echo ""
