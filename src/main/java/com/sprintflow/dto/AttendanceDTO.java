package com.sprintflow.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AttendanceDTO {

    private Long id;
    private Long sprintId;
    private String sprintTitle;
    private Long employeeId;
    private String empId;
    private String employeeName;
    private String technology;
    private String cohort;
    private LocalDate attendanceDate;
    private String status;       // Present, Late, Absent
    private String checkInTime;
    private String notes;
    private boolean submitted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── Submit request body ──────────────────────────────────
    public static class SubmitRequest {
        private Long sprintId;
        private LocalDate attendanceDate;
        private List<AttendanceRecord> records;

        public static class AttendanceRecord {
            private Long employeeId;
            private String status;      // Present, Late, Absent
            private String checkInTime;
            private String notes;

            public Long getEmployeeId() { return employeeId; }
            public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

            public String getStatus() { return status; }
            public void setStatus(String status) { this.status = status; }

            public String getCheckInTime() { return checkInTime; }
            public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }

            public String getNotes() { return notes; }
            public void setNotes(String notes) { this.notes = notes; }
        }

        public Long getSprintId() { return sprintId; }
        public void setSprintId(Long sprintId) { this.sprintId = sprintId; }

        public LocalDate getAttendanceDate() { return attendanceDate; }
        public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

        public List<AttendanceRecord> getRecords() { return records; }
        public void setRecords(List<AttendanceRecord> records) { this.records = records; }
    }

    // ── Stats response ───────────────────────────────────────
    public static class StatsDTO {
        private Long employeeId;
        private String empId;
        private String employeeName;
        private String cohort;
        private String technology;
        private long totalDays;
        private long presentDays;
        private long lateDays;
        private long absentDays;
        private double presentPercentage;

        public Long getEmployeeId() { return employeeId; }
        public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

        public String getEmpId() { return empId; }
        public void setEmpId(String empId) { this.empId = empId; }

        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

        public String getCohort() { return cohort; }
        public void setCohort(String cohort) { this.cohort = cohort; }

        public String getTechnology() { return technology; }
        public void setTechnology(String technology) { this.technology = technology; }

        public long getTotalDays() { return totalDays; }
        public void setTotalDays(long totalDays) { this.totalDays = totalDays; }

        public long getPresentDays() { return presentDays; }
        public void setPresentDays(long presentDays) { this.presentDays = presentDays; }

        public long getLateDays() { return lateDays; }
        public void setLateDays(long lateDays) { this.lateDays = lateDays; }

        public long getAbsentDays() { return absentDays; }
        public void setAbsentDays(long absentDays) { this.absentDays = absentDays; }

        public double getPresentPercentage() { return presentPercentage; }
        public void setPresentPercentage(double presentPercentage) { this.presentPercentage = presentPercentage; }
    }

    // ── Cohort stats response ────────────────────────────────
    public static class CohortStatsDTO {
        private String cohort;
        private String technology;
        private long totalDays;
        private long presentDays;
        private long lateDays;
        private long absentDays;
        private double presentPercentage;

        public String getCohort() { return cohort; }
        public void setCohort(String cohort) { this.cohort = cohort; }

        public String getTechnology() { return technology; }
        public void setTechnology(String technology) { this.technology = technology; }

        public long getTotalDays() { return totalDays; }
        public void setTotalDays(long totalDays) { this.totalDays = totalDays; }

        public long getPresentDays() { return presentDays; }
        public void setPresentDays(long presentDays) { this.presentDays = presentDays; }

        public long getLateDays() { return lateDays; }
        public void setLateDays(long lateDays) { this.lateDays = lateDays; }

        public long getAbsentDays() { return absentDays; }
        public void setAbsentDays(long absentDays) { this.absentDays = absentDays; }

        public double getPresentPercentage() { return presentPercentage; }
        public void setPresentPercentage(double presentPercentage) { this.presentPercentage = presentPercentage; }
    }

    // AttendanceDTO Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSprintId() { return sprintId; }
    public void setSprintId(Long sprintId) { this.sprintId = sprintId; }

    public String getSprintTitle() { return sprintTitle; }
    public void setSprintTitle(String sprintTitle) { this.sprintTitle = sprintTitle; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmpId() { return empId; }
    public void setEmpId(String empId) { this.empId = empId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getTechnology() { return technology; }
    public void setTechnology(String technology) { this.technology = technology; }

    public String getCohort() { return cohort; }
    public void setCohort(String cohort) { this.cohort = cohort; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCheckInTime() { return checkInTime; }
    public void setCheckInTime(String checkInTime) { this.checkInTime = checkInTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public boolean isSubmitted() { return submitted; }
    public void setSubmitted(boolean submitted) { this.submitted = submitted; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
