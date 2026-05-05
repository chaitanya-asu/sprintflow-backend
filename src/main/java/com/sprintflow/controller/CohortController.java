package com.sprintflow.controller;

import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.entity.Cohort;
import com.sprintflow.repository.CohortRepository;
import com.sprintflow.service.CohortService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cohorts")
@Tag(name = "Cohorts", description = "Management of training cohorts")
public class CohortController {

    private static final Logger logger = LoggerFactory.getLogger(CohortController.class);

    @Autowired
    private CohortRepository cohortRepository;
    
    @Autowired
    private CohortService cohortService;

    @Operation(summary = "Get all cohorts")
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<Cohort>>> getAll() {
        logger.info("Fetching all cohorts");
        return ok("Cohorts retrieved", cohortRepository.findAll());
    }
    
    @Operation(summary = "Get all cohorts with student information")
    @GetMapping("/with-students")
    public ResponseEntity<ApiResponseDTO<List<Map<String, Object>>>> getAllWithStudents() {
        logger.info("Fetching all cohorts with student information");
        try {
            List<Map<String, Object>> cohortsWithStudents = cohortService.getAllCohorts();
            return ok("Cohorts with students retrieved", cohortsWithStudents);
        } catch (Exception e) {
            logger.error("Error fetching cohorts with students", e);
            return ResponseEntity.status(500).body(ApiResponseDTO.<List<Map<String, Object>>>builder()
                    .success(false).message("Error fetching cohorts: " + e.getMessage()).statusCode(500).build());
        }
    }
    
    @Operation(summary = "Get cohort by technology and name with students")
    @GetMapping("/{technology}/{cohortName}")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> getCohortWithStudents(
            @Parameter(description = "Technology", example = "Java") @PathVariable String technology,
            @Parameter(description = "Cohort name", example = "C1") @PathVariable String cohortName) {
        logger.info("Fetching cohort: {} - {}", technology, cohortName);
        try {
            Map<String, Object> cohort = cohortService.getCohortByTechnologyAndName(technology, cohortName);
            return ok("Cohort retrieved", cohort);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid parameters: {}", e.getMessage());
            return ResponseEntity.status(400).body(ApiResponseDTO.<Map<String, Object>>builder()
                    .success(false).message(e.getMessage()).statusCode(400).build());
        } catch (Exception e) {
            logger.error("Error fetching cohort", e);
            return ResponseEntity.status(500).body(ApiResponseDTO.<Map<String, Object>>builder()
                    .success(false).message("Error fetching cohort: " + e.getMessage()).statusCode(500).build());
        }
    }
    
    @Operation(summary = "Get all technologies")
    @GetMapping("/technologies/list")
    public ResponseEntity<ApiResponseDTO<List<String>>> getAllTechnologies() {
        logger.info("Fetching all technologies");
        try {
            List<String> technologies = cohortService.getAllTechnologies();
            return ok("Technologies retrieved", technologies);
        } catch (Exception e) {
            logger.error("Error fetching technologies", e);
            return ResponseEntity.status(500).body(ApiResponseDTO.<List<String>>builder()
                    .success(false).message("Error fetching technologies: " + e.getMessage()).statusCode(500).build());
        }
    }
    
    @Operation(summary = "Get cohorts by technology")
    @GetMapping("/by-technology/{technology}")
    public ResponseEntity<ApiResponseDTO<List<String>>> getCohortsByTechnology(
            @Parameter(description = "Technology", example = "Java") @PathVariable String technology) {
        logger.info("Fetching cohorts for technology: {}", technology);
        try {
            List<String> cohorts = cohortService.getCohortsByTechnology(technology);
            return ok("Cohorts retrieved", cohorts);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid technology: {}", e.getMessage());
            return ResponseEntity.status(400).body(ApiResponseDTO.<List<String>>builder()
                    .success(false).message(e.getMessage()).statusCode(400).build());
        } catch (Exception e) {
            logger.error("Error fetching cohorts", e);
            return ResponseEntity.status(500).body(ApiResponseDTO.<List<String>>builder()
                    .success(false).message("Error fetching cohorts: " + e.getMessage()).statusCode(500).build());
        }
    }

    @Operation(summary = "Create a new cohort with technology and cohort number")
    @PostMapping
    public ResponseEntity<ApiResponseDTO<Cohort>> create(@RequestBody Map<String, String> request) {
        logger.info("Creating new cohort");
        try {
            String technology = request.get("technology");
            String cohortNumber = request.get("cohortNumber");
            
            if (technology == null || technology.isBlank() || cohortNumber == null || cohortNumber.isBlank()) {
                return ResponseEntity.status(400).body(ApiResponseDTO.<Cohort>builder()
                        .success(false).message("Technology and cohortNumber are required").statusCode(400).build());
            }
            
            Cohort cohort = cohortService.createCohort(technology, cohortNumber);
            return ok("Cohort created successfully", cohort);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid cohort creation request: {}", e.getMessage());
            return ResponseEntity.status(400).body(ApiResponseDTO.<Cohort>builder()
                    .success(false).message(e.getMessage()).statusCode(400).build());
        } catch (Exception e) {
            logger.error("Error creating cohort", e);
            return ResponseEntity.status(500).body(ApiResponseDTO.<Cohort>builder()
                    .success(false).message("Error creating cohort: " + e.getMessage()).statusCode(500).build());
        }
    }

    @Operation(summary = "Update an existing cohort")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Cohort>> update(@PathVariable Long id, @RequestBody Map<String, String> request) {
        logger.info("Updating cohort: {}", id);
        try {
            String technology = request.get("technology");
            String cohortNumber = request.get("cohortNumber");
            
            Cohort cohort = cohortService.updateCohort(id, technology, cohortNumber);
            return ok("Cohort updated successfully", cohort);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid cohort update request: {}", e.getMessage());
            return ResponseEntity.status(400).body(ApiResponseDTO.<Cohort>builder()
                    .success(false).message(e.getMessage()).statusCode(400).build());
        } catch (Exception e) {
            logger.error("Error updating cohort", e);
            return ResponseEntity.status(500).body(ApiResponseDTO.<Cohort>builder()
                    .success(false).message("Error updating cohort: " + e.getMessage()).statusCode(500).build());
        }
    }

    @Operation(summary = "Delete a cohort")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {
        logger.info("Deleting cohort: {}", id);
        try {
            cohortRepository.deleteById(id);
            return ok("Cohort deleted successfully", null);
        } catch (Exception e) {
            logger.error("Error deleting cohort", e);
            return ResponseEntity.status(500).body(ApiResponseDTO.<String>builder()
                    .success(false).message("Error deleting cohort: " + e.getMessage()).statusCode(500).build());
        }
    }

    private <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {
        return ResponseEntity.ok(ApiResponseDTO.<T>builder()
                .success(true).message(message).data(data).statusCode(200).build());
    }
}
