package com.sprintflow.controller;

import com.sprintflow.dto.ApiResponseDTO;
import com.sprintflow.entity.Cohort;
import com.sprintflow.repository.CohortRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cohorts")
@Tag(name = "Cohorts", description = "Management of training cohorts")
public class CohortController {

    @Autowired
    private CohortRepository cohortRepository;

    @Operation(summary = "Get all cohorts")
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<Cohort>>> getAll() {
        return ok("Cohorts retrieved", cohortRepository.findAll());
    }

    @Operation(summary = "Create a new cohort")
    @PostMapping
    public ResponseEntity<ApiResponseDTO<Cohort>> create(@RequestBody Cohort cohort) {
        return ok("Cohort created", cohortRepository.save(cohort));
    }

    @Operation(summary = "Update an existing cohort")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Cohort>> update(@PathVariable Long id, @RequestBody Cohort cohortData) {
        Cohort existing = cohortRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cohort not found: " + id));
        
        if (cohortData.getName() != null && !cohortData.getName().equals(existing.getName())) {
            if (cohortRepository.existsByName(cohortData.getName())) {
                return ResponseEntity.status(400).body(ApiResponseDTO.<Cohort>builder()
                        .success(false).message("Cohort name already exists: " + cohortData.getName()).statusCode(400).build());
            }
            existing.setName(cohortData.getName());
        }
        if (cohortData.getPatternType() != null) existing.setPatternType(cohortData.getPatternType());
        if (cohortData.getStatus() != null) existing.setStatus(cohortData.getStatus());
        
        return ok("Cohort updated", cohortRepository.save(existing));
    }

    @Operation(summary = "Delete a cohort")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<String>> delete(@PathVariable Long id) {
        cohortRepository.deleteById(id);
        return ok("Cohort deleted", null);
    }

    private <T> ResponseEntity<ApiResponseDTO<T>> ok(String message, T data) {


        return ResponseEntity.ok(ApiResponseDTO.<T>builder()
                .success(true).message(message).data(data).statusCode(200).build());
    }
}
