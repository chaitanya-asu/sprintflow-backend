package com.sprintflow.service;

import com.sprintflow.dto.AttendanceDTO;
import com.sprintflow.entity.Attendance;
import com.sprintflow.entity.Employee;
import com.sprintflow.entity.Sprint;
import com.sprintflow.entity.Notification;
import com.sprintflow.entity.User;
import com.sprintflow.exception.ResourceNotFoundException;
import com.sprintflow.repository.AttendanceRepository;
import com.sprintflow.repository.EmployeeRepository;
import com.sprintflow.repository.SprintEmployeeRepository;
import com.sprintflow.repository.SprintRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceService.class);
    private static final String ATTENDANCE_STATUS_PRESENT = "Present";
    private static final String ATTENDANCE_STATUS_DNA = "DNA";
    private static final String ATTENDANCE_STATUS_ABSENT = "Absent";

    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private SprintRepository     sprintRepository;
    @Autowired private EmployeeRepository   employeeRepository;
    @Autowired private SprintEmployeeRepository sprintEmployeeRepository;
    @Autowired private NotificationService   notificationService;
    @Autowired private WebSocketSyncService  webSocketSyncService;
    @Autowired(required = false) private EmailService emailService;

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

        boolean sendEmails = request.isSendAbsenceEmails();

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

            boolean isAbsent = ATTENDANCE_STATUS_ABSENT.equalsIgnoreCase(record.getStatus());

            // Notify critical absence (in-app)
            if (isAbsent) {
                try {
                    User creator = sprint.getCreatedBy();
                    if (creator != null) {
                        Notification notif = Notification.builder()
                            .userEmail(creator.getEmail())
                            .title("Employee Absence Alert")
                            .message(employee.getName() + " is absent from sprint: " + sprint.getTitle())
                            .type("WARNING")
                            .isRead(false)
                            .createdAt(LocalDateTime.now())
                            .build();
                        notificationService.create(notif);
                    }
                } catch (Exception e) {
                    logger.error("Error sending absence notification", e);
                }
            }

            // Send absence email if flag is set
            if (sendEmails && isAbsent && emailService != null && employee.getEmail() != null) {
                try {
                    String managerEmail = sprint.getCreatedBy() != null ? sprint.getCreatedBy().getEmail() : "Manager";
                    emailService.sendAbsenceNotification(
                        employee.getEmail(),
                        employee.getName(),
                        sprint.getTitle(),
                        request.getAttendanceDate().atStartOfDay(),
                        managerEmail
                    );
                } catch (Exception e) {
                    logger.error("Error sending absence email to {}: {}", employee.getEmail(), e.getMessage());
                }
            }
        }
        
        // Broadcast attendance update after all records processed
        try {
            webSocketSyncService.broadcastAttendanceUpdate(sprint.getId(), "SUBMIT", request);
        } catch (Exception e) {
            logger.error("Error broadcasting attendance update", e);
        }
    }

    @Transactional(readOnly = true)
    public List<AttendanceDTO> getByDate(Long sprintId, LocalDate date) {
        return attendanceRepository.findBySprintIdAndAttendanceDate(sprintId, date)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceDTO> getAllBySprint(Long sprintId) {
        return attendanceRepository.findBySprintId(sprintId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceDTO.StatsDTO> getStats(Long sprintId) {
        if (sprintId == null) {
            throw new IllegalArgumentException("Sprint ID cannot be null");
        }

        List<Object[]> rows = attendanceRepository.getStatsBySprintId(sprintId);
        return rows.stream().map(row -> {
            AttendanceDTO.StatsDTO stats = new AttendanceDTO.StatsDTO();
            Long empId = ((Number) row[0]).longValue();
            Employee e = employeeRepository.findById(empId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + empId));
            stats.setEmployeeId(e.getId());
            stats.setEmpId(e.getEmpId());
            stats.setEmployeeName(e.getName());
            stats.setCohort(e.getCohort());
            stats.setTechnology(e.getTechnology());

            long total = ((Number) row[1]).longValue();
            long present = ((Number) row[2]).longValue();
            long dna = ((Number) row[3]).longValue();
            long absent = ((Number) row[4]).longValue();

            stats.setTotalDays(total);
            stats.setPresentDays(present);
            stats.setLateDays(dna);
            stats.setAbsentDays(absent);
            stats.setPresentPercentage(calculatePercentage(present + dna, total));
            return stats;
        }).collect(Collectors.toList());
    }

    public List<AttendanceDTO.CohortStatsDTO> getCohortStats(Long sprintId) {
        if (sprintId == null) {
            throw new IllegalArgumentException("Sprint ID cannot be null");
        }

        List<Object[]> rows = attendanceRepository.getCohortStatsBySprintId(sprintId);
        return rows.stream().map(row -> {
            AttendanceDTO.CohortStatsDTO stats = new AttendanceDTO.CohortStatsDTO();
            stats.setCohort((String) row[0]);
            stats.setTechnology((String) row[1]);

            long total = ((Number) row[2]).longValue();
            long present = ((Number) row[3]).longValue();
            long dna = ((Number) row[4]).longValue();
            long absent = ((Number) row[5]).longValue();

            stats.setTotalDays(total);
            stats.setPresentDays(present);
            stats.setLateDays(dna);
            stats.setAbsentDays(absent);
            stats.setPresentPercentage(calculatePercentage(present + dna, total));
            return stats;
        }).collect(Collectors.toList());
    }

    public List<AttendanceDTO.CohortStatsDTO> getGlobalCohortStats() {
        List<Object[]> rows = attendanceRepository.getGlobalCohortStats();
        return rows.stream().map(row -> {
            AttendanceDTO.CohortStatsDTO stats = new AttendanceDTO.CohortStatsDTO();
            stats.setCohort((String) row[0]);
            stats.setTechnology((String) row[1]);

            long total = ((Number) row[2]).longValue();
            long present = ((Number) row[3]).longValue();
            long dna = ((Number) row[4]).longValue();
            long absent = ((Number) row[5]).longValue();

            stats.setTotalDays(total);
            stats.setPresentDays(present);
            stats.setLateDays(dna);
            stats.setAbsentDays(absent);
            stats.setPresentPercentage(calculatePercentage(present + dna, total));
            return stats;
        }).collect(Collectors.toList());
    }

    public AttendanceDTO.SummaryDTO getSummary() {
        long present = attendanceRepository.countByStatus(ATTENDANCE_STATUS_PRESENT);
        long dna = attendanceRepository.countByStatus(ATTENDANCE_STATUS_DNA);
        long absent = attendanceRepository.countByStatus(ATTENDANCE_STATUS_ABSENT);
        long total = present + dna + absent;

        AttendanceDTO.SummaryDTO summary = new AttendanceDTO.SummaryDTO();
        summary.setTotalRecords(total);
        summary.setPresentCount(present);
        summary.setLateCount(dna);
        summary.setAbsentCount(absent);
        summary.setOverallPresentPercentage(calculatePercentage(present + dna, total));
        return summary;
    }

    @Transactional(readOnly = true)
    public List<AttendanceDTO.LatestSnapshotDTO> getLatestAttendancePerSprint(List<com.sprintflow.entity.Sprint> sprints) {
        List<AttendanceDTO.LatestSnapshotDTO> snapshots = new java.util.ArrayList<>();
        for (com.sprintflow.entity.Sprint sprint : sprints) {
            AttendanceDTO.LatestSnapshotDTO snapshot = new AttendanceDTO.LatestSnapshotDTO();
            snapshot.setSprintId(sprint.getId());
            snapshot.setSprintTitle(sprint.getTitle());
            snapshot.setStatus(sprint.getStatus());

            LocalDate latestDate = attendanceRepository.findLatestSubmittedDateBySprintId(sprint.getId());
            if (latestDate == null) {
                snapshot.setAttendanceDate(null);
                snapshot.setEnrolled((int) sprintEmployeeRepository.countBySprintId(sprint.getId()));
                snapshot.setAttendanceRate(0);
                snapshots.add(snapshot);
                continue;
            }

            snapshot.setAttendanceDate(latestDate.toString());

            List<Object[]> statusCounts = attendanceRepository.getStatusCountsBySprintAndDate(sprint.getId(), latestDate);
            int present = 0, dnd = 0, absent = 0, onHold = 0, restricted = 0;
            for (Object[] row : statusCounts) {
                String status = (String) row[0];
                long count = ((Number) row[1]).longValue();
                switch (status) {
                    case "Present": present += (int) count; break;
                    case "DNA": dnd += (int) count; break;
                    case "Absent": absent += (int) count; break;
                    case "On Hold": onHold += (int) count; break;
                    case "Restricted": restricted += (int) count; break;
                }
            }

            int total = present + dnd + absent + onHold;
            snapshot.setEnrolled(total);
            snapshot.setPresent(present);
            snapshot.setDnd(dnd);
            snapshot.setAbsent(absent);
            snapshot.setOnHold(onHold);
            snapshot.setRestricted(restricted);
            snapshot.setAttendanceRate(total > 0 ? (int) Math.round(((double) (present + dnd) / total) * 100) : 0);
            snapshots.add(snapshot);
        }
        return snapshots;
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

    private double calculatePercentage(long numerator, long denominator) {
        return denominator == 0 ? 0.0 : (double) numerator / denominator * 100;
    }
}
