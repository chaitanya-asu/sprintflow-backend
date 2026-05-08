package com.sprintflow.repository;

import com.sprintflow.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // ── Single Record Queries ────────────────────────────────────
    
    Optional<Employee> findByEmpId(String empId);

    boolean existsByEmpId(String empId);

    boolean existsByEmail(String email);
    
    // ── Paginated Queries ───────────────────────────────────────
    
    Page<Employee> findAll(Pageable pageable);
    
    Page<Employee> findByTechnology(String technology, Pageable pageable);
    
    Page<Employee> findByCohort(String cohort, Pageable pageable);
    
    Page<Employee> findByTechnologyAndCohort(String technology, String cohort, Pageable pageable);
    
    Page<Employee> findByStatus(String status, Pageable pageable);
    
    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.empId) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Employee> searchByKeyword(@org.springframework.data.repository.query.Param("keyword") String keyword, Pageable pageable);
    
    // ── Non-Paginated Queries (for backward compatibility) ──────

    List<Employee> findByTechnology(String technology);

    List<Employee> findByCohort(String cohort);

    List<Employee> findByTechnologyAndCohort(String technology, String cohort);

    List<Employee> findByStatus(String status);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(e.empId) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<Employee> searchByKeyword(String keyword);

    @Query("SELECT e FROM Employee e WHERE e.technology = ?1 AND (e.cohort = ?2 OR CONCAT(e.technology, e.cohort) = ?2) AND e.status = 'Active'")
    List<Employee> findActiveByTechnologyAndCohort(String technology, String cohort);
    
    // ── Count Queries for Performance ──────────────────────────
    
    long countByTechnology(String technology);
    
    long countByCohort(String cohort);
    
    long countByTechnologyAndCohort(String technology, String cohort);
    
    long countByStatus(String status);
    
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.technology = :technology AND e.status = 'Active'")
    long countActiveByTechnology(@org.springframework.data.repository.query.Param("technology") String technology);
    
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.cohort = :cohort AND e.status = 'Active'")
    long countActiveByCohort(@org.springframework.data.repository.query.Param("cohort") String cohort);
}
