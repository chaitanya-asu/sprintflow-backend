# SprintFlow - Automated Fix Execution

## 🚀 Quick Start

Choose your operating system and follow the instructions below.

---

## Windows Users

### Option 1: Automated Script (Recommended)

```bash
# 1. Open Command Prompt in the project directory
cd C:\Users\Lenovo\Documents\workspace-spring-tools-for-eclipse-5.0.1.RELEASE\springboot\project\sprintflow

# 2. Run the automated script
RUN_FIXES.bat

# 3. Follow the prompts
# - Enter MySQL username (default: root)
# - Enter MySQL password
# - Wait for completion
```

### Option 2: Manual Steps

```bash
# 1. Backup database
mysqldump -u root -p sprintflow_db > backup.sql

# 2. Load clean seed data
mysql -u root -p sprintflow_db < CLEAN_SEED.sql

# 3. Rebuild project
mvn clean install -DskipTests

# 4. Start application
mvn spring-boot:run
```

---

## Linux/Mac Users

### Option 1: Automated Script (Recommended)

```bash
# 1. Navigate to project directory
cd /path/to/sprintflow

# 2. Make script executable
chmod +x run_fixes.sh

# 3. Run the automated script
./run_fixes.sh

# 4. Follow the prompts
# - Enter MySQL username (default: root)
# - Enter MySQL password
# - Wait for completion
```

### Option 2: Manual Steps

```bash
# 1. Backup database
mysqldump -u root -p sprintflow_db > backup.sql

# 2. Load clean seed data
mysql -u root -p sprintflow_db < CLEAN_SEED.sql

# 3. Rebuild project
mvn clean install -DskipTests

# 4. Start application
mvn spring-boot:run
```

---

## What the Script Does

The automated script performs these steps:

1. **Backup Database** - Creates a backup of your current database
2. **Load Seed Data** - Loads the clean seed data with standardized cohort names
3. **Verify Data** - Verifies that data was loaded correctly
4. **Rebuild Project** - Rebuilds the Maven project with all fixes
5. **Start Application** - Starts the Spring Boot application

---

## Expected Output

### After Script Completes

```
============================================================================
                         ✓ EXECUTION COMPLETE
============================================================================

Application is starting. Please wait for the message:
   "Started SprintFlowApplication"

Then test with:
   curl http://localhost:8080/api/employees

Default credentials:
   Email: s.lakkampally@ajacs.in
   Password: Admin@123
```

### In Application Logs

You should see:

```
[DataInitializer] Reset 5 user passwords to default
Started SprintFlowApplication in X.XXX seconds
```

---

## Verification

### Check Database

```bash
# Verify users
mysql -u root -p sprintflow_db -e "SELECT COUNT(*) FROM users;"
# Expected: 5

# Verify employees
mysql -u root -p sprintflow_db -e "SELECT COUNT(*) FROM employees;"
# Expected: 63

# Verify sprints
mysql -u root -p sprintflow_db -e "SELECT COUNT(*) FROM sprints;"
# Expected: 5

# Verify cohorts
mysql -u root -p sprintflow_db -e "SELECT technology, cohort, COUNT(*) FROM employees GROUP BY technology, cohort;"
# Expected: JavaC1 (12), JavaC2 (13), PythonC1 (10), DevopsC1 (8), DotNetC1 (8), SalesForceC1 (6)
```

### Test API

```bash
# Get all employees
curl http://localhost:8080/api/employees

# Get all sprints
curl http://localhost:8080/api/sprints

# Get cohorts
curl http://localhost:8080/api/cohorts
```

### Test Modules

1. **HR Module**
   - Login: s.lakkampally@ajacs.in / Admin@123
   - Navigate to Cohorts
   - Verify all 5 cohorts display with students

2. **Trainer Module**
   - Login: s.posanapally@ajacs.in / Admin@123
   - Navigate to Sprints
   - Verify students are enrolled
   - Mark attendance

3. **Manager Module**
   - Login: surya@sprintflow.com / Admin@123
   - View dashboard
   - Verify all data displays

---

## Troubleshooting

### Script Won't Run (Windows)

```bash
# If you get "is not recognized as an internal or external command"
# Make sure you're in the correct directory:
cd C:\Users\Lenovo\Documents\workspace-spring-tools-for-eclipse-5.0.1.RELEASE\springboot\project\sprintflow

# Then run:
RUN_FIXES.bat
```

### Script Won't Run (Linux/Mac)

```bash
# If you get "Permission denied"
# Make script executable:
chmod +x run_fixes.sh

# Then run:
./run_fixes.sh
```

### MySQL Connection Error

```bash
# If you get "Access denied for user"
# Check your MySQL credentials:
mysql -u root -p -e "SELECT 1;"

# If that works, try the script again
```

### Database Already Exists

```bash
# If you get "database already exists"
# The script will use the existing database
# Your old data will be replaced with clean seed data
# A backup is created automatically
```

### Maven Build Fails

```bash
# If you get Maven errors:
# 1. Make sure Java 21 is installed
java -version

# 2. Make sure Maven is installed
mvn -version

# 3. Try cleaning Maven cache
mvn clean

# 4. Try rebuilding
mvn clean install -DskipTests
```

### Application Won't Start

```bash
# Check logs for errors
# Look for "ERROR" or "Exception" in the output

# Common issues:
# 1. Port 8080 already in use
# 2. Database connection failed
# 3. Java version mismatch

# Try starting with verbose output:
mvn spring-boot:run -X
```

---

## Manual Execution (If Script Fails)

If the automated script doesn't work, follow these manual steps:

### Step 1: Backup Database

```bash
mysqldump -u root -p sprintflow_db > backup_manual.sql
```

### Step 2: Load Seed Data

```bash
mysql -u root -p sprintflow_db < CLEAN_SEED.sql
```

### Step 3: Verify Data

```bash
mysql -u root -p sprintflow_db -e "SELECT 'Users' AS table_name, COUNT(*) FROM users UNION ALL SELECT 'Employees', COUNT(*) FROM employees UNION ALL SELECT 'Sprints', COUNT(*) FROM sprints;"
```

### Step 4: Rebuild Project

```bash
mvn clean install -DskipTests
```

### Step 5: Start Application

```bash
mvn spring-boot:run
```

---

## Default Credentials

After running the script, use these credentials to login:

| Role | Email | Password |
|------|-------|----------|
| HR | s.lakkampally@ajacs.in | Admin@123 |
| Trainer | s.posanapally@ajacs.in | Admin@123 |
| Manager | surya@sprintflow.com | Admin@123 |

---

## Files Used by Script

- `CLEAN_SEED.sql` - Clean database setup
- `pom.xml` - Maven configuration
- `src/main/java/com/sprintflow/` - Updated Java files

---

## Support

If you encounter issues:

1. **Check logs** - Look for error messages
2. **Read documentation** - See INDEX.md for guides
3. **Verify database** - Run verification queries
4. **Check credentials** - Ensure MySQL username/password are correct
5. **Try manual steps** - Follow manual execution section

---

## Next Steps After Execution

1. ✅ Application is running
2. ✅ Database is populated
3. ✅ All fixes are applied
4. ✅ Test the modules (HR, Trainer, Manager)
5. ✅ Check logs for any errors
6. ✅ Monitor application performance

---

## Additional Resources

- `INDEX.md` - Navigation guide
- `QUICK_FIX_SUMMARY.md` - Quick reference
- `ISSUES_RESOLUTION_GUIDE.md` - Detailed explanations
- `EXECUTION_GUIDE.md` - Step-by-step instructions
- `COMPLETE_RESOLUTION_SUMMARY.md` - Complete overview

---

**Status:** ✅ Ready to Execute
**Version:** 1.0
**Last Updated:** 2024

---

**Happy coding! 🚀**
