package com.sprintflow.service;

import com.sprintflow.dto.RoomDTO;
import com.sprintflow.entity.Room;
import com.sprintflow.exception.DuplicateResourceException;
import com.sprintflow.exception.ResourceNotFoundException;
import com.sprintflow.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Get all rooms
     */
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get room by ID
     */
    public RoomDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        return convertToDTO(room);
    }

    /**
     * Get room by name
     */
    public RoomDTO getRoomByName(String name) {
        Room room = roomRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with name: " + name));
        return convertToDTO(room);
    }

    /**
     * Create a new room
     */
    public RoomDTO createRoom(RoomDTO roomDTO) {
        // Validate input
        if (roomDTO.getName() == null || roomDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Room name is required");
        }
        if (roomDTO.getCapacity() == null || roomDTO.getCapacity() < 1) {
            throw new IllegalArgumentException("Room capacity must be at least 1");
        }

        // Check for duplicate name
        if (roomRepository.findByNameIgnoreCase(roomDTO.getName()).isPresent()) {
            throw new DuplicateResourceException("Room with name '" + roomDTO.getName() + "' already exists");
        }

        Room room = new Room();
        room.setName(roomDTO.getName().trim());
        room.setCapacity(roomDTO.getCapacity());
        room.setStatus(roomDTO.getStatus() != null ? roomDTO.getStatus() : "Active");
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());

        Room savedRoom = roomRepository.save(room);
        RoomDTO savedDto = convertToDTO(savedRoom);
        
        // Broadcast event
        messagingTemplate.convertAndSend("/topic/rooms", Map.of(
                "type", "ROOM_CREATED",
                "payload", savedDto
        ));
        
        return savedDto;
    }

    /**
     * Update an existing room
     */
    public RoomDTO updateRoom(Long id, RoomDTO roomDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));

        // Validate input
        if (roomDTO.getName() == null || roomDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Room name is required");
        }
        if (roomDTO.getCapacity() == null || roomDTO.getCapacity() < 1) {
            throw new IllegalArgumentException("Room capacity must be at least 1");
        }

        // Check for duplicate name (excluding current room)
        roomRepository.findByNameIgnoreCase(roomDTO.getName())
                .ifPresent(existingRoom -> {
                    if (!existingRoom.getId().equals(id)) {
                        throw new DuplicateResourceException("Room with name '" + roomDTO.getName() + "' already exists");
                    }
                });

        room.setName(roomDTO.getName().trim());
        room.setCapacity(roomDTO.getCapacity());
        if (roomDTO.getStatus() != null) {
            room.setStatus(roomDTO.getStatus());
        }
        room.setUpdatedAt(LocalDateTime.now());

        Room updatedRoom = roomRepository.save(room);
        RoomDTO updatedDto = convertToDTO(updatedRoom);
        
        // Broadcast event
        messagingTemplate.convertAndSend("/topic/rooms", Map.of(
                "type", "ROOM_UPDATED",
                "payload", updatedDto
        ));
        
        return updatedDto;
    }

    /**
     * Delete a room
     */
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + id));
        roomRepository.delete(room);
        
        // Broadcast event
        messagingTemplate.convertAndSend("/topic/rooms", Map.of(
                "type", "ROOM_DELETED",
                "payload", id
        ));
    }

    /**
     * Convert Room entity to RoomDTO
     */
    private RoomDTO convertToDTO(Room room) {
        return new RoomDTO(
                room.getId(),
                room.getName(),
                room.getCapacity(),
                room.getStatus()
        );
    }
}
