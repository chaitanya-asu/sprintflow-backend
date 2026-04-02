package com.sprintflow.controller;

import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.dto.EmployeeDTO;
import com.sprintflow.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, allowCredentials = "true")
public class EmployeeController {

    @Autowired private EmployeeService employeeService;

    // GET /api/employees?technology=Java&cohort=Cohort A
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<EmployeeDTO>>> getAll(
            @RequestParam(required = false) String technology,
            @RequestParam(required = false) String cohort) {
        List<EmployeeDTO> list = (technology != null && cohort != null)
                ? employeeService.getByTechnologyAndCohort(technology, cohort)
                : employeeService.getAllEmployees();
        return ok("Employees retrieved successfully", list);
    }

    // GET /api/employees/search?keyword=alice&technology=Java
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<List<EmployeeDTO>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String technology) {
        return ok("Search results", employeeService.search(keyword, technology));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EmployeeDTO>> getById(@PathVariable Long id) {
        return ok("Employee retrieved successfully", employeeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<EmployeeDTO>> create(@RequestBody EmployeeDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponseDTO.<EmployeeDTO>builder()
                .success(true).message("Employee created successfully")
                .data(employeeService.create(dto)).statusCode(201).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EmployeeDTO>> update(
            @PathVariable Long id, @RequestBody EmployeeDTO dto) {
        return ok("Employee updated successfully", employeeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ok("Employee deleted successfully", null);
    }

    // ── Sprint enrollment ─────────────────────────────────────

    // GET /api/sprints/:id/employees
    @GetMapping("/sprint/{sprintId}")
    public ResponseEntity<ApiResponseDTO<List<EmployeeDTO>>> getSprintEmployees(@PathVariable Long sprintId) {
        return ok("Sprint employees retrieved successfully", employeeService.getSprintEmployees(sprintId));
    }

    // POST /api/sprints/:id/employees  { employeeId }
    @PostMapping("/sprint/{sprintId}/enroll")
    public ResponseEntity<ApiResponseDTO<String>> enroll(
            @PathVariable Long sprintId, @RequestBody Map<String, Long> body) {
        employeeService.enrollEmployee(sprintId, body.get("employeeId"));
        return ok("Employee enrolled successfully", null);
    }

    // DELETE /api/sprints/:id/employees/:empId
    @DeleteMapping("/sprint/{sprintId}/remove/{employeeId}")
    public ResponseEntity<ApiResponseDTO<String>> remove(
            @PathVariable Long sprintId, @PathVariable Long employeeId) {
        employeeService.removeEmployee(sprintId, employeeId);
        return ok("Employee removed from sprint", null);
    }

    // Auto-enroll all matching employees for a sprint's cohort pairs
    @PostMapping("/sprint/{sprintId}/auto-enroll")
    public ResponseEntity<ApiResponseDTO<String>> autoEnroll(@PathVariable Long sprintId) {
        employeeService.autoEnrollByCohorts(sprintId);
        return ok("Employees auto-enrolled by cohort pairs", null);
    }

    // ── Helpers ──────────────────────────────────────────────

    private <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponseDTO.<T>builder()
                .success(true).message(message).data(data).statusCode(200).build());
    }
}
