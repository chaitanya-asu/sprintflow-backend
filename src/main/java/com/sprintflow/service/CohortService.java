package com.sprintflow.service;

import com.sprintflow.entity.Cohort;
import com.sprintflow.repository.CohortRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CohortService {

    @Autowired
    private CohortRepository cohortRepository;

    public List<Cohort> getAll() {
        return cohortRepository.findAll();
    }

    public Cohort create(Cohort cohort) {
        return cohortRepository.save(cohort);
    }

    public void delete(Long id) {
        cohortRepository.deleteById(id);
    }
}
