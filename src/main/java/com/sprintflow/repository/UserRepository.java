package com.sprintflow.repository;

import com.sprintflow.entity.Role;
import com.sprintflow.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role = ?1 AND u.status = 'Active'")
    List<User> findByRole(Role role);

    @Query("SELECT u FROM User u WHERE u.status = 'Active'")
    List<User> findAllActive();
}
