package com.sprintflow.repository;

import com.sprintflow.entity.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {
    
    @Query("SELECT g FROM ChatGroup g JOIN g.members m WHERE m.email = :email")
    List<ChatGroup> findByMemberEmail(@Param("email") String email);
}
