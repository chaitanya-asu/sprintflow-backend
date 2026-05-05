package com.sprintflow.repository;

import com.sprintflow.entity.MasterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MasterDataRepository extends JpaRepository<MasterData, Long> {
    List<MasterData> findByCategory(String category);
    List<MasterData> findByCategoryAndParentValue(String category, String parentValue);
}
