package com.sprintflow.service;

import com.sprintflow.dto.EmployeeDTO;
import com.sprintflow.entity.Employee;
import com.sprintflow.entity.Sprint;
import com.sprintflow.entity.SprintEmployee;
import com.sprintflow.exception.DuplicateResourceException;
import com.sprintflow.exception.ResourceNotFoundException;
import com.sprintflow.repository.EmployeeRepository;
import com.sprintflow.repository.SprintEmployeeRepository;
import com.sprintflow.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private SprintRepository sprintRepository;
    @Autowired private SprintEmployeeRepository sprintEmployeeRepository;

    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Returns only Active employees — used by default GET /api/employees
    public List<EmployeeDTO> getActiveEmployees() {
        return employeeRepository.findByStatus("Active").stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<EmployeeDTO> getByTechnologyAndCohort(String technology, String cohort) {
        if (technology == null || technology.isBlank() || cohort == null || cohort.isBlank()) {
            throw new IllegalArgumentException("Technology and cohort cannot be null or empty");
        }
        return employeeRepository.findByTechnologyAndCohort(technology, cohort)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<EmployeeDTO> search(String keyword, String technology) {
        List<Employee> results = keyword != null && !keyword.isBlank()
                ? employeeRepository.searchByKeyword(keyword)
                : employeeRepository.findAll();
        if (technology != null && !technology.isBlank())
            results = results.stream().filter(e -> e.getTechnology().equalsIgnoreCase(technology)).collect(Collectors.toList());
        return results.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public EmployeeDTO getById(Long id) {
        return toDTO(employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id)));
    }

    public EmployeeDTO create(EmployeeDTO dto) {
        if (employeeRepository.existsByEmpId(dto.getEmpId()))
            throw new DuplicateResourceException("Employee ID already exists: " + dto.getEmpId());
        if (dto.getEmail() != null && employeeRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateResourceException("Email already exists: " + dto.getEmail());

        Employee emp = new Employee();
        mapDtoToEntity(dto, emp);
        emp.setCreatedAt(LocalDateTime.now());
        emp.setUpdatedAt(LocalDateTime.now());
        return toDTO(employeeRepository.save(emp));
    }

    public EmployeeDTO update(Long id, EmployeeDTO dto) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        mapDtoToEntity(dto, emp);
        emp.setUpdatedAt(LocalDateTime.now());
        return toDTO(employeeRepository.save(emp));
    }

    public void delete(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        emp.setStatus("Inactive");
        emp.setUpdatedAt(LocalDateTime.now());
        employeeRepository.save(emp);
    }

    // ── Sprint enrollment ─────────────────────────────────────

    public List<EmployeeDTO> getSprintEmployees(Long sprintId) {
        return sprintEmployeeRepository.findBySprintId(sprintId).stream()
                .map(se -> toDTO(se.getEmployee()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void enrollEmployee(Long sprintId, Long employeeId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + sprintId));
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
        if (!sprintEmployeeRepository.existsBySprintIdAndEmployeeId(sprintId, employeeId)) {
            sprintEmployeeRepository.save(new SprintEmployee(sprint, emp));
        }
    }

    @Transactional
    public void removeEmployee(Long sprintId, Long employeeId) {
        sprintEmployeeRepository.deleteBySprintIdAndEmployeeId(sprintId, employeeId);
    }

    @Transactional
    public void autoEnrollByCohorts(Long sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + sprintId));

        if (sprint.getCohortsJson() != null && !sprint.getCohortsJson().isBlank()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
                List<com.sprintflow.dto.SprintDTO.CohortPair> pairs = om.readValue(
                        sprint.getCohortsJson(),
                        new com.fasterxml.jackson.core.type.TypeReference<List<com.sprintflow.dto.SprintDTO.CohortPair>>() {});
                
                for (com.sprintflow.dto.SprintDTO.CohortPair pair : pairs) {
                    if (pair.getTechnology() != null && pair.getCohort() != null) {
                        List<Employee> matched = employeeRepository.findActiveByTechnologyAndCohort(
                                pair.getTechnology(), pair.getCohort());
                        for (Employee emp : matched) {
                            if (!sprintEmployeeRepository.existsBySprintIdAndEmployeeId(sprintId, emp.getId())) {
                                sprintEmployeeRepository.save(new SprintEmployee(sprint, emp));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Error auto-enrolling employees: " + e.getMessage(), e);
            }
        }
    }

    // ── Helpers ──────────────────────────────────────────────

    private void mapDtoToEntity(EmployeeDTO dto, Employee emp) {
        if (dto.getEmpId()      != null && !dto.getEmpId().isBlank())      emp.setEmpId(dto.getEmpId());
        if (dto.getName()       != null && !dto.getName().isBlank())       emp.setName(dto.getName());
        if (dto.getEmail()      != null && !dto.getEmail().isBlank())      emp.setEmail(dto.getEmail());
        if (dto.getPhone()      != null && !dto.getPhone().isBlank())      emp.setPhone(dto.getPhone());
        if (dto.getTechnology() != null && !dto.getTechnology().isBlank()) emp.setTechnology(dto.getTechnology());
        if (dto.getCohort()     != null && !dto.getCohort().isBlank())     emp.setCohort(dto.getCohort());
        if (dto.getDepartment() != null && !dto.getDepartment().isBlank()) emp.setDepartment(dto.getDepartment());
        if (dto.getStatus()     != null && !dto.getStatus().isBlank())     emp.setStatus(dto.getStatus());
    }

    public EmployeeDTO toDTO(Employee emp) {
        if (emp == null) {
            return null;
        }
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(emp.getId());
        dto.setEmpId(emp.getEmpId());
        dto.setName(emp.getName());
        dto.setEmail(emp.getEmail());
        dto.setPhone(emp.getPhone());
        dto.setTechnology(emp.getTechnology());
        dto.setCohort(emp.getCohort());
        dto.setDepartment(emp.getDepartment());
        dto.setStatus(emp.getStatus());
        dto.setCreatedAt(emp.getCreatedAt());
        dto.setUpdatedAt(emp.getUpdatedAt());
        return dto;
    }
}
