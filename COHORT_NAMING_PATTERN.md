# Cohort Naming Pattern — SprintFlow

## Overview
All cohorts must follow the standardized pattern: **C{number}** (e.g., C1, C2, C3, C10, etc.)

## Valid Examples
- ✅ C1
- ✅ C2
- ✅ C3
- ✅ C4
- ✅ C10
- ✅ C99

## Invalid Examples (Rejected)
- ❌ JC1, JC2, JC3, JC4 (old Java cohort pattern)
- ❌ PC1, PC2 (old Python cohort pattern)
- ❌ DC1, DC2 (old DevOps cohort pattern)
- ❌ Cohort A, Cohort B (descriptive names)
- ❌ C (missing number)
- ❌ 1, 2, 3 (missing C prefix)

## Enforcement Layers

### 1. Database Level (MySQL CHECK Constraint)
```sql
ALTER TABLE employees 
ADD CONSTRAINT chk_cohort_pattern 
CHECK (cohort REGEXP '^C[0-9]+$');
```
- Prevents invalid cohorts at the database level
- Applied via Flyway migration: `V7__Add_Cohort_Pattern_Constraint.sql`

### 2. Application Level (Java Validation)
- **Annotation**: `@ValidCohort` (custom validator)
- **Location**: `com.sprintflow.validation.ValidCohort`
- **Applied to**:
  - `Employee.java` entity field
  - `EmployeeDTO.java` request DTO field
- **Validation Logic**:
  - Accepts: `C1`, `C2`, `C3`, etc.
  - Rejects: `JC1`, `PC1`, `DC1`, empty values, null

### 3. API Response
When an invalid cohort is submitted:
```json
{
  "status": "VALIDATION_ERROR",
  "message": "Validation failed",
  "errors": {
    "cohort": "Cohort must follow pattern C1, C2, C3, etc. (e.g., 'C1', 'C2'). Old patterns like JC1, PC1, DC1 are no longer allowed."
  }
}
```

## Migration History
- **V6__Standardize_Cohort_Names.sql**: Migrated all existing cohorts to new pattern
  - JC2 → C2
  - JC3 → C3
  - JC4 → C4
  - PC1 → C1
  - DC1 → C1

- **V7__Add_Cohort_Pattern_Constraint.sql**: Added database constraint to prevent future invalid entries

## For Developers
When creating or updating employees:
```java
// ✅ Correct
employee.setCohort("C1");
employee.setCohort("C5");

// ❌ Wrong (will be rejected)
employee.setCohort("JC1");
employee.setCohort("PC1");
employee.setCohort("Cohort A");
```

## Testing
To verify the constraint works:
```bash
# This will fail (constraint violation)
INSERT INTO employees (emp_id, name, email, technology, cohort, status) 
VALUES ('TEST001', 'Test User', 'test@example.com', 'Java', 'JC1', 'Active');

# This will succeed
INSERT INTO employees (emp_id, name, email, technology, cohort, status) 
VALUES ('TEST001', 'Test User', 'test@example.com', 'Java', 'C1', 'Active');
```
