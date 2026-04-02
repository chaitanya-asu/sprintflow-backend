package com.sprintflow.repository;

import com.sprintflow.entity.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    List<Sprint> findByStatus(String status);

    List<Sprint> findByTrainerId(Long trainerId);

    @Query("SELECT s FROM Sprint s WHERE s.trainer.id = ?1 AND s.status = ?2")
    List<Sprint> findByTrainerIdAndStatus(Long trainerId, String status);

    @Query("SELECT s FROM Sprint s WHERE s.technology = ?1")
    List<Sprint> findByTechnology(String technology);

    @Query("SELECT s FROM Sprint s WHERE s.status IN ('Scheduled', 'On Hold') ORDER BY s.startDate ASC")
    List<Sprint> findActiveOrUpcoming();
}
