package com.sprintflow.repository;

import com.sprintflow.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Get all records for a sprint on a specific date
    List<Attendance> findBySprintIdAndAttendanceDate(Long sprintId, LocalDate date);

    // Get all records for a sprint
    List<Attendance> findBySprintId(Long sprintId);

    // Get all records for an employee in a sprint
    List<Attendance> findBySprintIdAndEmployeeId(Long sprintId, Long employeeId);

    // Check if already submitted for a date
    boolean existsBySprintIdAndAttendanceDateAndSubmittedTrue(Long sprintId, LocalDate date);

    // Unique constraint check
    Optional<Attendance> findBySprintIdAndEmployeeIdAndAttendanceDate(Long sprintId, Long employeeId, LocalDate date);

    // Stats per employee for a sprint
    @Query("SELECT a.employee.id, COUNT(a), " +
           "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'Late' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'Absent' THEN 1 ELSE 0 END) " +
           "FROM Attendance a WHERE a.sprint.id = ?1 GROUP BY a.employee.id")
    List<Object[]> getStatsBySprintId(Long sprintId);

    // Cohort-level stats for a sprint
    @Query("SELECT a.employee.cohort, a.employee.technology, COUNT(a), " +
           "SUM(CASE WHEN a.status = 'Present' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'Late' THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.status = 'Absent' THEN 1 ELSE 0 END) " +
           "FROM Attendance a WHERE a.sprint.id = ?1 GROUP BY a.employee.cohort, a.employee.technology")
    List<Object[]> getCohortStatsBySprintId(Long sprintId);
}
