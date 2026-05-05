package com.sprintflow.service;

import com.sprintflow.dto.AttendanceDTO;
import com.sprintflow.entity.Attendance;
import com.sprintflow.entity.Employee;
import com.sprintflow.entity.Sprint;
import com.sprintflow.exception.ResourceNotFoundException;
import com.sprintflow.repository.AttendanceRepository;
import com.sprintflow.repository.EmployeeRepository;
import com.sprintflow.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);
    private static final String ATTENDANCE_STATUS_PRESENT = "Present";
    private static final String ATTENDANCE_STATUS_LATE = "Late";
    private static final String ATTENDANCE_STATUS_ABSENT = "Absent";

    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private SprintRepository     sprintRepository;
    @Autowired private EmployeeRepository   employeeRepository;

    @Transactional
    public void submitAttendance(AttendanceDTO.SubmitRequest request, Long userId) {
        if (request == null || request.getSprintId() == null) {
            throw new IllegalArgumentException("Invalid attendance request");
        }

        Sprint sprint = sprintRepository.findById(request.getSprintId())
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + request.getSprintId()));

        if (request.getRecords() == null || request.getRecords().isEmpty()) {
            logger.warn("No attendance records provided for sprint: {}", request.getSprintId());
            return;
        }

        for (AttendanceDTO.SubmitRequest.AttendanceRecord record : request.getRecords()) {
            Employee employee = employeeRepository.findById(record.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + record.getEmployeeId()));

            Attendance attendance = attendanceRepository
                    .findBySprintIdAndEmployeeIdAndAttendanceDate(sprint.getId(), employee.getId(), request.getAttendanceDate())
                    .orElse(new Attendance());

            attendance.setSprint(sprint);
            attendance.setEmployee(employee);
            attendance.setAttendanceDate(request.getAttendanceDate());
            attendance.setStatus(record.getStatus());
            attendance.setCheckInTime(record.getCheckInTime());
            attendance.setNotes(record.getNotes());
            attendance.setSubmitted(true);

            attendanceRepository.save(attendance);
        }
    }

    public List<AttendanceDTO> getByDate(Long sprintId, LocalDate date) {
        return attendanceRepository.findBySprintIdAndAttendanceDate(sprintId, date)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AttendanceDTO> getAllBySprint(Long sprintId) {
        return attendanceRepository.findBySprintId(sprintId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<AttendanceDTO.StatsDTO> getStats(Long sprintId) {
        if (sprintId == null) {
            throw new IllegalArgumentException("Sprint ID cannot be null");
        }

        List<Attendance> list = attendanceRepository.findBySprintId(sprintId);
        Map<Employee, List<Attendance>> grouped = list.stream().collect(Collectors.groupingBy(Attendance::getEmployee));

        return grouped.entrySet().stream().map(entry -> {
            Employee e = entry.getKey();
            List<Attendance> atts = entry.getValue();
            AttendanceDTO.StatsDTO stats = new AttendanceDTO.StatsDTO();
            stats.setEmployeeId(e.getId());
            stats.setEmpId(e.getEmpId());
            stats.setEmployeeName(e.getName());
            stats.setCohort(e.getCohort());
            stats.setTechnology(e.getTechnology());
            
            long total = atts.size();
            long present = atts.stream().filter(a -> ATTENDANCE_STATUS_PRESENT.equalsIgnoreCase(a.getStatus())).count();
            long late = atts.stream().filter(a -> ATTENDANCE_STATUS_LATE.equalsIgnoreCase(a.getStatus())).count();
            long absent = atts.stream().filter(a -> ATTENDANCE_STATUS_ABSENT.equalsIgnoreCase(a.getStatus())).count();
            
            stats.setTotalDays(total);
            stats.setPresentDays(present);
            stats.setLateDays(late);
            stats.setAbsentDays(absent);
            stats.setPresentPercentage(calculatePercentage(present + late, total));
            return stats;
        }).collect(Collectors.toList());
    }

    public List<AttendanceDTO.CohortStatsDTO> getCohortStats(Long sprintId) {
        if (sprintId == null) {
            throw new IllegalArgumentException("Sprint ID cannot be null");
        }

        List<Attendance> list = attendanceRepository.findBySprintId(sprintId);
        return list.stream()
                .collect(Collectors.groupingBy(a -> buildCohortKey(a.getEmployee())))
                .entrySet().stream().map(entry -> {
                    String[] parts = entry.getKey().split("\\|");
                    List<Attendance> atts = entry.getValue();
                    AttendanceDTO.CohortStatsDTO stats = new AttendanceDTO.CohortStatsDTO();
                    stats.setCohort(parts[0]);
                    stats.setTechnology(parts.length > 1 ? parts[1] : "—");
                    
                    long total = atts.size();
                    long present = atts.stream().filter(a -> ATTENDANCE_STATUS_PRESENT.equalsIgnoreCase(a.getStatus())).count();
                    long late = atts.stream().filter(a -> ATTENDANCE_STATUS_LATE.equalsIgnoreCase(a.getStatus())).count();
                    long absent = atts.stream().filter(a -> ATTENDANCE_STATUS_ABSENT.equalsIgnoreCase(a.getStatus())).count();

                    stats.setTotalDays(total);
                    stats.setPresentDays(present);
                    stats.setLateDays(late);
                    stats.setAbsentDays(absent);
                    stats.setPresentPercentage(calculatePercentage(present + late, total));
                    return stats;
                }).collect(Collectors.toList());
    }

    public List<AttendanceDTO.CohortStatsDTO> getGlobalCohortStats() {
        List<Attendance> all = attendanceRepository.findAll();
        return all.stream()
                .collect(Collectors.groupingBy(a -> buildCohortKey(a.getEmployee())))
                .entrySet().stream().map(entry -> {
                    String[] parts = entry.getKey().split("\\|");
                    List<Attendance> atts = entry.getValue();
                    AttendanceDTO.CohortStatsDTO stats = new AttendanceDTO.CohortStatsDTO();
                    stats.setCohort(parts[0]);
                    stats.setTechnology(parts.length > 1 ? parts[1] : "—");
                    long total = atts.size();
                    long present = atts.stream().filter(a -> ATTENDANCE_STATUS_PRESENT.equalsIgnoreCase(a.getStatus())).count();
                    long late = atts.stream().filter(a -> ATTENDANCE_STATUS_LATE.equalsIgnoreCase(a.getStatus())).count();
                    long absent = atts.stream().filter(a -> ATTENDANCE_STATUS_ABSENT.equalsIgnoreCase(a.getStatus())).count();
                    stats.setTotalDays(total);
                    stats.setPresentDays(present);
                    stats.setLateDays(late);
                    stats.setAbsentDays(absent);
                    stats.setPresentPercentage(calculatePercentage(present + late, total));
                    return stats;
                }).collect(Collectors.toList());
    }

    public AttendanceDTO.SummaryDTO getSummary() {
        List<Attendance> all = attendanceRepository.findAll();
        AttendanceDTO.SummaryDTO summary = new AttendanceDTO.SummaryDTO();
        long total = all.size();
        long present = all.stream().filter(a -> ATTENDANCE_STATUS_PRESENT.equalsIgnoreCase(a.getStatus())).count();
        long late = all.stream().filter(a -> ATTENDANCE_STATUS_LATE.equalsIgnoreCase(a.getStatus())).count();
        long absent = all.stream().filter(a -> ATTENDANCE_STATUS_ABSENT.equalsIgnoreCase(a.getStatus())).count();
        
        summary.setTotalRecords(total);
        summary.setPresentCount(present);
        summary.setLateCount(late);
        summary.setAbsentCount(absent);
        summary.setOverallPresentPercentage(calculatePercentage(present + late, total));
        return summary;
    }

    private AttendanceDTO toDTO(Attendance a) {
        if (a == null || a.getSprint() == null || a.getEmployee() == null) {
            logger.warn("Null attendance or related entities encountered");
            return null;
        }

        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(a.getId());
        dto.setSprintId(a.getSprint().getId());
        dto.setSprintTitle(a.getSprint().getTitle());
        dto.setEmployeeId(a.getEmployee().getId());
        dto.setEmpId(a.getEmployee().getEmpId());
        dto.setEmployeeName(a.getEmployee().getName());
        dto.setTechnology(a.getEmployee().getTechnology());
        dto.setCohort(a.getEmployee().getCohort());
        dto.setAttendanceDate(a.getAttendanceDate());
        dto.setStatus(a.getStatus());
        dto.setCheckInTime(a.getCheckInTime());
        dto.setNotes(a.getNotes());
        dto.setSubmitted(a.isSubmitted());
        dto.setCreatedAt(a.getCreatedAt());
        dto.setUpdatedAt(a.getUpdatedAt());
        return dto;
    }

    private String buildCohortKey(Employee employee) {
        String cohort = employee.getCohort() != null ? employee.getCohort() : "Unknown";
        String technology = employee.getTechnology() != null ? employee.getTechnology() : "Unknown";
        return cohort + "|" + technology;
    }

    private double calculatePercentage(long numerator, long denominator) {
        return denominator == 0 ? 0.0 : (double) numerator / denominator * 100;
    }
}
