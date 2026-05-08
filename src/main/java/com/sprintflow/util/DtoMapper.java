package com.sprintflow.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprintflow.dto.AttendanceDTO;
import com.sprintflow.dto.EmployeeDTO;
import com.sprintflow.dto.SprintDTO;
import com.sprintflow.entity.Attendance;
import com.sprintflow.entity.Employee;
import com.sprintflow.entity.Sprint;

/**
 * Centralized DTO mapping utility
 * Ensures consistent data transformation between entities and DTOs
 * Handles field name inconsistencies and null safety
 */
@Component
public class DtoMapper {

    private static final Logger logger = LoggerFactory.getLogger(DtoMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── Employee Mapping ──────────────────────────────────────────────────────

    public EmployeeDTO toEmployeeDTO(Employee employee) {
        if (employee == null) return null;

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setEmpId(employee.getEmpId());
        dto.setName(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        dto.setTechnology(employee.getTechnology());
        dto.setCohort(employee.getCohort());
        dto.setDepartment(employee.getDepartment());
        dto.setStatus(employee.getStatus());
        dto.setBlocked(employee.isBlocked());
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());
        return dto;
    }

    public List<EmployeeDTO> toEmployeeDTOList(List<Employee> employees) {
        if (employees == null) return new ArrayList<>();
        return employees.stream()
                .map(this::toEmployeeDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    public Employee toEmployeeEntity(EmployeeDTO dto) {
        if (dto == null) return null;

        Employee employee = new Employee();
        employee.setId(dto.getId());
        employee.setEmpId(dto.getEmpId());
        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setTechnology(dto.getTechnology());
        employee.setCohort(dto.getCohort());
        employee.setDepartment(dto.getDepartment());
        employee.setStatus(dto.getStatus() != null ? dto.getStatus() : "Active");
        employee.setBlocked(dto.isBlocked());
        return employee;
    }

    // ── Sprint Mapping ────────────────────────────────────────────────────────

    public SprintDTO toSprintDTO(Sprint sprint) {
        if (sprint == null) return null;

        SprintDTO dto = new SprintDTO();
        dto.setId(sprint.getId());
        dto.setTitle(sprint.getTitle());
        dto.setSprintType(sprint.getSprintType());
        dto.setTechnology(sprint.getTechnology());
        dto.setSprintSubject(sprint.getSprintSubject());
        dto.setSprintCommunicationType(sprint.getSprintCommunicationType());
        dto.setCohort(sprint.getCohort());
        dto.setStartDate(sprint.getStartDate());
        dto.setEndDate(sprint.getEndDate());
        dto.setSprintStart(sprint.getSprintStart());
        dto.setSprintEnd(sprint.getSprintEnd());
        dto.setRoom(sprint.getRoom());
        dto.setStatus(sprint.getStatus());
        dto.setInstructions(sprint.getInstructions());
        dto.setCreatedAt(sprint.getCreatedAt());
        dto.setUpdatedAt(sprint.getUpdatedAt());

        // Map trainer
        if (sprint.getTrainer() != null) {
            dto.setTrainerId(sprint.getTrainer().getId());
            dto.setTrainer(sprint.getTrainer().getName());
        }

        // Map employee count
        if (sprint.getEnrollments() != null) {
            dto.setEmployeeCount(sprint.getEnrollments().size());
        } else {
            dto.setEmployeeCount(0);
        }

        // Parse cohorts JSON
        if (sprint.getCohortsJson() != null && !sprint.getCohortsJson().isBlank()) {
            try {
                List<SprintDTO.CohortPair> cohorts = objectMapper.readValue(
                        sprint.getCohortsJson(),
                        new TypeReference<List<SprintDTO.CohortPair>>() {}
                );
                dto.setCohorts(cohorts);
            } catch (Exception e) {
                logger.error("Failed to parse cohorts JSON for sprint {}: {}", sprint.getId(), e.getMessage());
                dto.setCohorts(new ArrayList<>());
            }
        } else {
            dto.setCohorts(new ArrayList<>());
        }

        // Compute time slot
        if (sprint.getSprintStart() != null && sprint.getSprintEnd() != null) {
            dto.setTimeSlot(sprint.getSprintStart() + " - " + sprint.getSprintEnd());
        }

        return dto;
    }

    public List<SprintDTO> toSprintDTOList(List<Sprint> sprints) {
        if (sprints == null) return new ArrayList<>();
        return sprints.stream()
                .map(this::toSprintDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    public Sprint toSprintEntity(SprintDTO dto) {
        if (dto == null) return null;

        Sprint sprint = new Sprint();
        sprint.setId(dto.getId());
        sprint.setTitle(dto.getTitle());
        sprint.setSprintType(dto.getSprintType());
        sprint.setTechnology(dto.getTechnology());
        sprint.setSprintSubject(dto.getSprintSubject());
        sprint.setSprintCommunicationType(dto.getSprintCommunicationType());
        sprint.setCohort(dto.getCohort());
        sprint.setStartDate(dto.getStartDate());
        sprint.setEndDate(dto.getEndDate());
        sprint.setSprintStart(dto.getSprintStart());
        sprint.setSprintEnd(dto.getSprintEnd());
        sprint.setRoom(dto.getRoom());
        sprint.setStatus(dto.getStatus() != null ? dto.getStatus() : "Scheduled");
        sprint.setInstructions(dto.getInstructions());

        // Serialize cohorts to JSON
        if (dto.getCohorts() != null && !dto.getCohorts().isEmpty()) {
            try {
                sprint.setCohortsJson(objectMapper.writeValueAsString(dto.getCohorts()));
            } catch (Exception e) {
                logger.error("Failed to serialize cohorts JSON: {}", e.getMessage());
            }
        }

        return sprint;
    }

    // ── Attendance Mapping ────────────────────────────────────────────────────

    public AttendanceDTO toAttendanceDTO(Attendance attendance) {
        if (attendance == null) return null;

        AttendanceDTO dto = new AttendanceDTO();
        dto.setId(attendance.getId());
        dto.setSprintId(attendance.getSprint() != null ? attendance.getSprint().getId() : null);
        dto.setSprintTitle(attendance.getSprint() != null ? attendance.getSprint().getTitle() : null);
        dto.setEmployeeId(attendance.getEmployee() != null ? attendance.getEmployee().getId() : null);
        dto.setEmpId(attendance.getEmployee() != null ? attendance.getEmployee().getEmpId() : null);
        dto.setEmployeeName(attendance.getEmployee() != null ? attendance.getEmployee().getName() : null);
        dto.setTechnology(attendance.getEmployee() != null ? attendance.getEmployee().getTechnology() : null);
        dto.setCohort(attendance.getEmployee() != null ? attendance.getEmployee().getCohort() : null);
        dto.setAttendanceDate(attendance.getAttendanceDate());
        dto.setStatus(attendance.getStatus());
        dto.setCheckInTime(attendance.getCheckInTime());
        dto.setNotes(attendance.getNotes());
        dto.setSubmitted(attendance.isSubmitted());
        dto.setCreatedAt(attendance.getCreatedAt());
        dto.setUpdatedAt(attendance.getUpdatedAt());
        return dto;
    }

    public List<AttendanceDTO> toAttendanceDTOList(List<Attendance> attendanceList) {
        if (attendanceList == null) return new ArrayList<>();
        return attendanceList.stream()
                .map(this::toAttendanceDTO)
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }
}
