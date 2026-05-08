package com.sprintflow.repository;

import com.sprintflow.entity.Sprint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    // ── Paginated Queries ──────────────────────────────────────
    
    Page<Sprint> findAll(Pageable pageable);
    
    Page<Sprint> findByStatus(String status, Pageable pageable);
    
    Page<Sprint> findByTrainerId(Long trainerId, Pageable pageable);
    
    Page<Sprint> findByTechnology(String technology, Pageable pageable);
    
    @Query("SELECT s FROM Sprint s WHERE s.status IN ('Scheduled', 'On Hold') ORDER BY s.startDate ASC")
    Page<Sprint> findActiveOrUpcoming(Pageable pageable);
    
    // ── Non-Paginated Queries (for backward compatibility) ──────
    
    List<Sprint> findByStatus(String status);

    List<Sprint> findByTrainerId(Long trainerId);

    @Query("SELECT s FROM Sprint s WHERE s.trainer.id = ?1 AND s.status = ?2")
    List<Sprint> findByTrainerIdAndStatus(Long trainerId, String status);

    @Query("SELECT s FROM Sprint s WHERE s.technology = ?1")
    List<Sprint> findByTechnology(String technology);

    @Query("SELECT s FROM Sprint s WHERE s.status IN ('Scheduled', 'On Hold') ORDER BY s.startDate ASC")
    List<Sprint> findActiveOrUpcoming();
    
    // ── Optimized Queries with EntityGraph (N+1 prevention) ────
    
    @EntityGraph(attributePaths = {"trainer", "enrollments", "createdBy"})
    @Query("SELECT s FROM Sprint s WHERE s.id = :id")
    Optional<Sprint> findByIdWithDetails(@org.springframework.data.repository.query.Param("id") Long id);
    
    @Query("SELECT s FROM Sprint s WHERE s.trainer.id = :trainerId")
    List<Sprint> findByTrainerIdWithTrainer(@org.springframework.data.repository.query.Param("trainerId") Long trainerId);
    
    @EntityGraph(attributePaths = {"trainer", "enrollments"})
    Page<Sprint> findAllBy(Pageable pageable);

    @Query("SELECT s FROM Sprint s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:technology IS NULL OR s.technology = :technology) AND " +
           "(:q IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(s.trainer.name) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Sprint> findByFilters(
            @org.springframework.data.repository.query.Param("status") String status,
            @org.springframework.data.repository.query.Param("technology") String technology,
            @org.springframework.data.repository.query.Param("q") String q,
            Pageable pageable);

    // ── Trainer Conflict Detection ─────────────────────────────
    
    // Find sprints for a trainer whose date range overlaps with the given range
    // Used for trainer time-slot conflict checking
    @Query("SELECT s FROM Sprint s WHERE s.trainer.id = :trainerId " +
           "AND s.status != 'Completed' " +
           "AND s.startDate <= :endDate " +
           "AND s.endDate >= :startDate " +
           "AND (:excludeId IS NULL OR s.id != :excludeId)")
    List<Sprint> findTrainerOverlappingSprints(
            @org.springframework.data.repository.query.Param("trainerId")  Long trainerId,
            @org.springframework.data.repository.query.Param("startDate")  java.time.LocalDate startDate,
            @org.springframework.data.repository.query.Param("endDate")    java.time.LocalDate endDate,
            @org.springframework.data.repository.query.Param("excludeId")  Long excludeId);
    
    // ── Count Queries for Performance ──────────────────────────
    
    long countByStatus(String status);
    
    long countByTrainerId(Long trainerId);
    
    long countByTechnology(String technology);
    
    @Query("SELECT COUNT(s) FROM Sprint s WHERE s.startDate <= :endDate AND s.endDate >= :startDate")
    long countActiveInDateRange(
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate);
}
