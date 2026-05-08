package com.sprintflow.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sprintflow.dto.EmployeeDTO;
import com.sprintflow.entity.Employee;
import com.sprintflow.entity.Sprint;
import com.sprintflow.entity.SprintEmployee;
import com.sprintflow.exception.DuplicateResourceException;
import com.sprintflow.exception.ResourceNotFoundException;
import com.sprintflow.repository.EmployeeRepository;
import com.sprintflow.repository.SprintEmployeeRepository;
import com.sprintflow.repository.SprintRepository;
import com.sprintflow.util.DtoMapper;

@Service
public class EmployeeService {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private SprintRepository sprintRepository;
    @Autowired private SprintEmployeeRepository sprintEmployeeRepository;
    @Autowired private DtoMapper dtoMapper;

    public List<EmployeeDTO> getAllEmployees() {
        return dtoMapper.toEmployeeDTOList(employeeRepository.findAll());
    }

    // Returns only Active employees — used by default GET /api/employees
    public List<EmployeeDTO> getActiveEmployees() {
        return dtoMapper.toEmployeeDTOList(employeeRepository.findByStatus("Active"));
    }

    public List<EmployeeDTO> getByTechnologyAndCohort(String technology, String cohort) {
        if (technology == null || technology.isBlank() || cohort == null || cohort.isBlank()) {
            throw new IllegalArgumentException("Technology and cohort cannot be null or empty");
        }
        return dtoMapper.toEmployeeDTOList(
                employeeRepository.findByTechnologyAndCohort(technology, cohort));
    }

    public List<EmployeeDTO> search(String keyword, String technology) {
        List<Employee> results = keyword != null && !keyword.isBlank()
                ? employeeRepository.searchByKeyword(keyword)
                : employeeRepository.findAll();
        if (technology != null && !technology.isBlank())
            results = results.stream().filter(e -> e.getTechnology().equalsIgnoreCase(technology)).collect(Collectors.toList());
        return dtoMapper.toEmployeeDTOList(results);
    }

    public EmployeeDTO getById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        return dtoMapper.toEmployeeDTO(employee);
    }

    public EmployeeDTO create(EmployeeDTO dto) {
        if (employeeRepository.existsByEmpId(dto.getEmpId()))
            throw new DuplicateResourceException("Employee ID already exists: " + dto.getEmpId());
        if (dto.getEmail() != null && employeeRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateResourceException("Email already exists: " + dto.getEmail());

        Employee emp = dtoMapper.toEmployeeEntity(dto);
        emp.setCreatedAt(LocalDateTime.now());
        emp.setUpdatedAt(LocalDateTime.now());
        Employee saved = employeeRepository.save(emp);
        return dtoMapper.toEmployeeDTO(saved);
    }

    public EmployeeDTO update(Long id, EmployeeDTO dto) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        
        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(emp.getEmail())
                && employeeRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + dto.getEmail());
        }

        if (dto.getEmpId() != null && !dto.getEmpId().equals(emp.getEmpId())
                && employeeRepository.existsByEmpId(dto.getEmpId())) {
            throw new DuplicateResourceException("Employee ID already exists: " + dto.getEmpId());
        }
        
        if (dto.getEmpId() != null) emp.setEmpId(dto.getEmpId());
        if (dto.getName() != null) emp.setName(dto.getName());
        if (dto.getEmail() != null) emp.setEmail(dto.getEmail());
        if (dto.getPhone() != null) emp.setPhone(dto.getPhone());
        if (dto.getTechnology() != null) emp.setTechnology(dto.getTechnology());
        if (dto.getCohort() != null) emp.setCohort(dto.getCohort());
        if (dto.getDepartment() != null) emp.setDepartment(dto.getDepartment());
        if (dto.getStatus() != null) emp.setStatus(dto.getStatus());
        
        emp.setUpdatedAt(LocalDateTime.now());
        Employee saved = employeeRepository.save(emp);
        return dtoMapper.toEmployeeDTO(saved);
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
        List<Employee> employees = sprintEmployeeRepository.findBySprintId(sprintId).stream()
                .map(SprintEmployee::getEmployee)
                .collect(Collectors.toList());
        return dtoMapper.toEmployeeDTOList(employees);
    }

    public List<Map<String, Object>> getSprintEmployeesWithBlockedStatus(Long sprintId) {
        List<SprintEmployee> enrollments = sprintEmployeeRepository.findBySprintId(sprintId);
        return enrollments.stream().map(se -> {
            Employee emp = se.getEmployee();
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", emp.getId());
            map.put("empId", emp.getEmpId());
            map.put("name", emp.getName());
            map.put("email", emp.getEmail());
            map.put("technology", emp.getTechnology());
            map.put("cohort", emp.getCohort());
            map.put("status", emp.getStatus());
            map.put("blocked", se.isBlocked());
            map.put("blockedReason", se.getBlockedReason());
            map.put("enrollmentStatus", se.getStatus());
            return map;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void enrollEmployee(Long sprintId, Long employeeId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + sprintId));
        Employee emp = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + employeeId));
        if (emp.isBlocked()) {
            throw new IllegalStateException("Employee is blocked and cannot be enrolled in sprints");
        }
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
                            if (!emp.isBlocked() && !sprintEmployeeRepository.existsBySprintIdAndEmployeeId(sprintId, emp.getId())) {
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

    // ── Employee blocking ─────────────────────────────────────

    @Transactional
    public EmployeeDTO toggleBlock(Long id, boolean blocked) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        emp.setBlocked(blocked);
        emp.setUpdatedAt(LocalDateTime.now());

        if (blocked) {
            // Remove from all active sprint enrollments
            List<SprintEmployee> enrollments = sprintEmployeeRepository.findByEmployeeId(emp.getId());
            for (SprintEmployee se : enrollments) {
                if ("ENROLLED".equals(se.getStatus())) {
                    se.setStatus("DROPPED");
                    sprintEmployeeRepository.save(se);
                }
            }
        }

        Employee saved = employeeRepository.save(emp);
        return dtoMapper.toEmployeeDTO(saved);
    }

}
