package com.sprintflow.service;

import com.sprintflow.entity.Cohort;
import com.sprintflow.entity.Employee;
import com.sprintflow.repository.CohortRepository;
import com.sprintflow.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CohortService {

    private static final Logger logger = LoggerFactory.getLogger(CohortService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CohortRepository cohortRepository;

    /**
     * Get all unique cohorts with employee counts
     */
    public List<Map<String, Object>> getAllCohorts() {
        List<Cohort> cohorts = cohortRepository.findAll();
        List<Employee> allEmployees = employeeRepository.findAll();
        
        return cohorts.stream()
                .map(cohort -> {
                    List<Employee> students = allEmployees.stream()
                            .filter(e -> e.getTechnology().equalsIgnoreCase(cohort.getTechnology()) && 
                                       e.getCohort().equalsIgnoreCase(cohort.getCohortNumber()))
                            .collect(Collectors.toList());
                            
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", cohort.getId());
                    map.put("name", cohort.getName());
                    map.put("technology", cohort.getTechnology());
                    map.put("cohort", cohort.getCohortNumber());
                    map.put("status", cohort.getStatus());
                    map.put("studentCount", students.size());
                    map.put("students", students.stream()
                            .map(e -> Map.of(
                                    "id", e.getId(),
                                    "empId", e.getEmpId(),
                                    "name", e.getName(),
                                    "email", e.getEmail(),
                                    "status", e.getStatus()
                            ))
                            .collect(Collectors.toList()));
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get cohort by technology and cohort name
     */
    public Map<String, Object> getCohortByTechnologyAndName(String technology, String cohort) {
        if (technology == null || technology.isBlank() || cohort == null || cohort.isBlank()) {
            throw new IllegalArgumentException("Technology and cohort name are required");
        }

        List<Employee> employees = employeeRepository.findByTechnologyAndCohort(technology, cohort);
        if (employees.isEmpty()) {
            logger.warn("No employees found for technology: {} and cohort: {}", technology, cohort);
            return Map.of(
                    "technology", technology,
                    "cohort", cohort,
                    "studentCount", 0,
                    "students", List.of()
            );
        }

        Map<String, Object> result = new HashMap<>();
        result.put("technology", technology);
        result.put("cohort", cohort);
        result.put("studentCount", employees.size());
        result.put("students", employees.stream()
                .map(e -> Map.of(
                        "id", e.getId(),
                        "empId", e.getEmpId(),
                        "name", e.getName(),
                        "email", e.getEmail(),
                        "phone", e.getPhone() != null ? e.getPhone() : "",
                        "department", e.getDepartment() != null ? e.getDepartment() : "",
                        "status", e.getStatus()
                ))
                .collect(Collectors.toList()));
        return result;
    }

    /**
     * Get all unique technologies
     */
    public List<String> getAllTechnologies() {
        return employeeRepository.findAll().stream()
                .map(Employee::getTechnology)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get cohorts by technology
     */
    public List<String> getCohortsByTechnology(String technology) {
        if (technology == null || technology.isBlank()) {
            throw new IllegalArgumentException("Technology is required");
        }
        return employeeRepository.findByTechnology(technology).stream()
                .map(Employee::getCohort)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Create a new cohort with technology and cohort number
     */
    public Cohort createCohort(String technology, String cohortNumber) {
        if (technology == null || technology.isBlank() || cohortNumber == null || cohortNumber.isBlank()) {
            throw new IllegalArgumentException("Technology and cohort number are required");
        }

        String name = technology + cohortNumber;
        
        if (cohortRepository.existsByName(name)) {
            throw new IllegalArgumentException("Cohort already exists: " + name);
        }

        Cohort cohort = Cohort.builder()
                .name(name)
                .technology(technology)
                .cohortNumber(cohortNumber)
                .patternType(technology)
                .status("Planned")
                .build();

        return cohortRepository.save(cohort);
    }

    /**
     * Update an existing cohort
     */
    public Cohort updateCohort(Long id, String technology, String cohortNumber) {
        Cohort cohort = cohortRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cohort not found: " + id));

        if (technology != null && !technology.isBlank()) {
            cohort.setTechnology(technology);
            cohort.setPatternType(technology);
        }
        if (cohortNumber != null && !cohortNumber.isBlank()) {
            cohort.setCohortNumber(cohortNumber);
        }

        // Name will be auto-updated by @PreUpdate
        return cohortRepository.save(cohort);
    }
}
