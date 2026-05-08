package com.sprintflow.repository;

import com.sprintflow.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // ── Basic Queries ───────────────────────────────────────────
    
    List<Attendance> findBySprintIdAndAttendanceDate(Long sprintId, LocalDate date);

    List<Attendance> findBySprintId(Long sprintId);

    List<Attendance> findBySprintIdAndEmployeeId(Long sprintId, Long employeeId);

    boolean existsBySprintIdAndAttendanceDateAndSubmittedTrue(Long sprintId, LocalDate date);

    Optional<Attendance> findBySprintIdAndEmployeeIdAndAttendanceDate(
            Long sprintId, Long employeeId, LocalDate date);

    long countByStatus(String status);
    
    // ── Paginated Queries ───────────────────────────────────────
    
    Page<Attendance> findBySprintId(Long sprintId, Pageable pageable);
    
    Page<Attendance> findByEmployeeId(Long employeeId, Pageable pageable);
    
    Page<Attendance> findBySprintIdAndEmployeeId(Long sprintId, Long employeeId, Pageable pageable);
    
    Page<Attendance> findByStatus(String status, Pageable pageable);
    
    @Query("SELECT a FROM Attendance a WHERE a.attendanceDate BETWEEN :startDate AND :endDate")
    Page<Attendance> findByDateRange(
            @org.springframework.data.repository.query.Param("startDate") LocalDate startDate,
            @org.springframework.data.repository.query.Param("endDate") LocalDate endDate,
            Pageable pageable);
    
    // ── Optimized Queries with EntityGraph (N+1 prevention) ────
    
    @EntityGraph(attributePaths = {"sprint", "employee"})
    @Query("SELECT a FROM Attendance a WHERE a.sprint.id = :sprintId AND a.attendanceDate = :date")
    List<Attendance> findBySprintIdAndDateWithDetails(
            @org.springframework.data.repository.query.Param("sprintId") Long sprintId,
            @org.springframework.data.repository.query.Param("date") LocalDate date);
    
    @EntityGraph(attributePaths = {"sprint", "employee"})
    @Query("SELECT a FROM Attendance a WHERE a.sprint.id = :sprintId")
    List<Attendance> findBySprintIdWithDetails(
            @org.springframework.data.repository.query.Param("sprintId") Long sprintId);

    // ── Statistics Queries ────────────────────────────────────
    
    // Per-employee stats for a sprint
    @Query("SELECT a.employee.id, COUNT(a), " +
           "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'DNA'    THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'Absent' THEN 1 ELSE 0 END) " +
           "FROM Attendance a WHERE a.sprint.id = ?1 GROUP BY a.employee.id")
    List<Object[]> getStatsBySprintId(Long sprintId);

    // Cohort-level stats for a sprint
    @Query("SELECT a.employee.cohort, a.employee.technology, COUNT(a), " +
           "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'DNA'    THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'Absent' THEN 1 ELSE 0 END) " +
           "FROM Attendance a WHERE a.sprint.id = ?1 GROUP BY a.employee.cohort, a.employee.technology")
    List<Object[]> getCohortStatsBySprintId(Long sprintId);

    // Global cohort-level stats across ALL sprints
    @Query("SELECT a.employee.cohort, a.employee.technology, COUNT(a), " +
           "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'DNA'    THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'Absent' THEN 1 ELSE 0 END) " +
           "FROM Attendance a GROUP BY a.employee.cohort, a.employee.technology")
    List<Object[]> getGlobalCohortStats();
    
    // ── Count Queries for Performance ──────────────────────────
    
    long countBySprintId(Long sprintId);
    
    long countByEmployeeId(Long employeeId);
    
    long countBySprintIdAndStatus(Long sprintId, String status);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.sprint.id = :sprintId AND a.attendanceDate = :date")
    long countBySprintIdAndDate(
            @org.springframework.data.repository.query.Param("sprintId") Long sprintId,
            @org.springframework.data.repository.query.Param("date") LocalDate date);
    
    @Query("SELECT COUNT(DISTINCT a.attendanceDate) FROM Attendance a WHERE a.sprint.id = :sprintId")
    long countDistinctDatesBySprintId(@org.springframework.data.repository.query.Param("sprintId") Long sprintId);

    // ── Latest Attendance Queries ─────────────────────────────

    @Query("SELECT MAX(a.attendanceDate) FROM Attendance a WHERE a.sprint.id = :sprintId AND a.submitted = true")
    LocalDate findLatestSubmittedDateBySprintId(@org.springframework.data.repository.query.Param("sprintId") Long sprintId);

    @Query("SELECT a.status, COUNT(a) FROM Attendance a WHERE a.sprint.id = :sprintId AND a.attendanceDate = :date AND a.submitted = true GROUP BY a.status")
    List<Object[]> getStatusCountsBySprintAndDate(
            @org.springframework.data.repository.query.Param("sprintId") Long sprintId,
            @org.springframework.data.repository.query.Param("date") LocalDate date);
}
