package com.sprintflow.repository;

import com.sprintflow.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByName(String name);
    Optional<Room> findByNameIgnoreCase(String name);
}
