package com.sprintflow.service;

import com.sprintflow.dto.AttendanceDTO;
import com.sprintflow.entity.Attendance;
import com.sprintflow.entity.Employee;
import com.sprintflow.entity.Sprint;
import com.sprintflow.entity.User;
import com.sprintflow.exception.ResourceNotFoundException;
import com.sprintflow.repository.AttendanceRepository;
import com.sprintflow.repository.EmployeeRepository;
import com.sprintflow.repository.SprintRepository;
import com.sprintflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private SprintRepository sprintRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private UserRepository userRepository;

    // Submit attendance for a date (trainer marks all students)
    @Transactional
    public void submitAttendance(AttendanceDTO.SubmitRequest request, Long markedByUserId) {
        Sprint sprint = sprintRepository.findById(request.getSprintId())
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + request.getSprintId()));
        User markedBy = userRepository.findById(markedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + markedByUserId));

        for (AttendanceDTO.SubmitRequest.AttendanceRecord rec : request.getRecords()) {
            Employee emp = employeeRepository.findById(rec.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + rec.getEmployeeId()));

            Attendance att = attendanceRepository
                    .findBySprintIdAndEmployeeIdAndAttendanceDate(sprint.getId(), emp.getId(), request.getAttendanceDate())
                    .orElse(new Attendance());

            att.setSprint(sprint);
            att.setEmployee(emp);
            att.setAttendanceDate(request.getAttendanceDate());
            att.setStatus(rec.getStatus());
            att.setCheckInTime(rec.getCheckInTime());
            att.setNotes(rec.getNotes());
            att.setSubmitted(true);
            att.setMarkedBy(markedBy);
            if (att.getId() == null) att.setCreatedAt(LocalDateTime.now());
            att.setUpdatedAt(LocalDateTime.now());

            attendanceRepository.save(att);
        }
    }

    // Get attendance by sprint + date
    public List<AttendanceDTO> getByDate(Long sprintId, LocalDate date) {
        return attendanceRepository.findBySprintIdAndAttendanceDate(sprintId, date)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Get stats per employee for a sprint
    public List<AttendanceDTO.StatsDTO> getStats(Long sprintId) {
        List<Object[]> raw = attendanceRepository.getStatsBySprintId(sprintId);
        List<AttendanceDTO.StatsDTO> stats = new ArrayList<>();
        for (Object[] row : raw) {
            Long empId = (Long) row[0];
            Employee emp = employeeRepository.findById(empId).orElse(null);
            if (emp == null) continue;

            AttendanceDTO.StatsDTO stat = new AttendanceDTO.StatsDTO();
            stat.setEmployeeId(empId);
            stat.setEmpId(emp.getEmpId());
            stat.setEmployeeName(emp.getName());
            stat.setCohort(emp.getCohort());
            stat.setTechnology(emp.getTechnology());
            stat.setTotalDays((Long) row[1]);
            stat.setPresentDays((Long) row[2]);
            stat.setLateDays((Long) row[3]);
            stat.setAbsentDays((Long) row[4]);
            stat.setPresentPercentage(stat.getTotalDays() > 0
                    ? Math.round((stat.getPresentDays() * 100.0 / stat.getTotalDays()) * 100) / 100.0
                    : 0);
            stats.add(stat);
        }
        return stats;
    }

    // Get cohort-level stats for a sprint
    public List<AttendanceDTO.CohortStatsDTO> getCohortStats(Long sprintId) {
        List<Object[]> raw = attendanceRepository.getCohortStatsBySprintId(sprintId);
        List<AttendanceDTO.CohortStatsDTO> stats = new ArrayList<>();
        for (Object[] row : raw) {
            AttendanceDTO.CohortStatsDTO stat = new AttendanceDTO.CohortStatsDTO();
            stat.setCohort((String) row[0]);
            stat.setTechnology((String) row[1]);
            stat.setTotalDays((Long) row[2]);
            stat.setPresentDays((Long) row[3]);
            stat.setLateDays((Long) row[4]);
            stat.setAbsentDays((Long) row[5]);
            stat.setPresentPercentage(stat.getTotalDays() > 0
                    ? Math.round((stat.getPresentDays() * 100.0 / stat.getTotalDays()) * 100) / 100.0
                    : 0);
            stats.add(stat);
        }
        return stats;
    }

    // ── Helpers ──────────────────────────────────────────────

    AttendanceDTO toDTO(Attendance att) {
        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(att.getId());
        dto.setSprintId(att.getSprint().getId());
        dto.setSprintTitle(att.getSprint().getTitle());
        dto.setEmployeeId(att.getEmployee().getId());
        dto.setEmpId(att.getEmployee().getEmpId());
        dto.setEmployeeName(att.getEmployee().getName());
        dto.setTechnology(att.getEmployee().getTechnology());
        dto.setCohort(att.getEmployee().getCohort());
        dto.setAttendanceDate(att.getAttendanceDate());
        dto.setStatus(att.getStatus());
        dto.setCheckInTime(att.getCheckInTime());
        dto.setNotes(att.getNotes());
        dto.setSubmitted(att.isSubmitted());
        dto.setCreatedAt(att.getCreatedAt());
        dto.setUpdatedAt(att.getUpdatedAt());
        return dto;
    }
}
