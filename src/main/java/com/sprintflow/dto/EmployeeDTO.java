package com.sprintflow.dto;

import java.time.LocalDateTime;
import com.sprintflow.validation.ValidCohort;

public class EmployeeDTO {

    private Long id;
    private String empId;       // EMP001
    private String name;
    private String email;
    private String phone;
    private String technology;  // Java, Python, Devops, DotNet, SalesForce
    @ValidCohort
    private String cohort;      // Must follow pattern: C1, C2, C3, etc.
    private String department;
    private String status;      // Active, Inactive
    private boolean blocked;    // Blocked from all sprints
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmployeeDTO() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }

    public String getCohort() { return cohort; }
    public void setCohort(String cohort) { this.cohort = cohort; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
