package com.sprintflow.service;

import com.sprintflow.dto.SprintDTO;
import com.sprintflow.dto.RoomAvailabilityDTO;
import com.sprintflow.entity.Sprint;
import com.sprintflow.entity.User;
import com.sprintflow.exception.ResourceNotFoundException;
import com.sprintflow.repository.SprintRepository;
import com.sprintflow.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SprintService {

    @Autowired private SprintRepository sprintRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private EmployeeService employeeService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<SprintDTO> getAllSprints() {
        return sprintRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<SprintDTO> getSprintsByStatus(String status) {
        return sprintRepository.findByStatus(status).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public SprintDTO getSprintById(Long id) {
        return toDTO(sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + id)));
    }

    public List<SprintDTO> getSprintsForTrainer(Long trainerId) {
        return sprintRepository.findByTrainerId(trainerId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public SprintDTO createSprint(SprintDTO dto, Long createdBy) {
        Sprint sprint = new Sprint();
        mapDtoToEntity(dto, sprint);
        
        User creator = userRepository.findById(createdBy).orElse(null);
        sprint.setCreatedBy(creator);
        sprint.setCreatedAt(LocalDateTime.now());
        sprint.setUpdatedAt(LocalDateTime.now());

        Sprint saved = sprintRepository.save(sprint);
        
        // Auto-enroll employees based on cohorts
        employeeService.autoEnrollByCohorts(saved.getId());

        // Notify trainer
        if (saved.getTrainer() != null) {
            com.sprintflow.entity.Notification notification = com.sprintflow.entity.Notification.builder()
                .userEmail(saved.getTrainer().getEmail())
                .title("New Sprint Assigned")
                .message("You have been assigned as a trainer for: " + saved.getTitle())
                .type("INFO")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
            notificationService.create(notification);
        }

        return toDTO(saved);
    }

    @Transactional
    public SprintDTO updateSprint(Long id, SprintDTO dto) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + id));
        mapDtoToEntity(dto, sprint);
        sprint.setUpdatedAt(LocalDateTime.now());
        return toDTO(sprintRepository.save(sprint));
    }

    @Transactional
    public SprintDTO updateStatus(Long id, String status) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + id));
        sprint.setStatus(status);
        sprint.setUpdatedAt(LocalDateTime.now());
        return toDTO(sprintRepository.save(sprint));
    }

    public void deleteSprint(Long id) {
        if (!sprintRepository.existsById(id))
            throw new ResourceNotFoundException("Sprint not found: " + id);
        sprintRepository.deleteById(id);
    }

    public List<SprintDTO> checkTrainerConflict(Long trainerId, LocalDate startDate, LocalDate endDate, 
                                               String sprintStart, String sprintEnd, Long excludeSprintId) {
        return sprintRepository.findTrainerOverlappingSprints(trainerId, startDate, endDate, excludeSprintId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<RoomAvailabilityDTO> getRoomAvailability() {
        List<Sprint> allSprints = sprintRepository.findAll();
        // Group by room
        return allSprints.stream()
                .filter(s -> s.getRoom() != null)
                .collect(Collectors.groupingBy(Sprint::getRoom))
                .entrySet().stream()
                .map(entry -> {
                    String roomName = entry.getKey();
                    List<RoomAvailabilityDTO.BookingDTO> bookings = entry.getValue().stream()
                            .map(s -> new RoomAvailabilityDTO.BookingDTO(
                                    s.getId(),
                                    s.getTitle(),
                                    s.getTrainer() != null ? s.getTrainer().getName() : "Unknown",
                                    s.getStartDate().toString(),
                                    s.getEndDate().toString(),
                                    s.getSprintStart(),
                                    s.getSprintEnd()
                            ))
                            .collect(Collectors.toList());
                    return new RoomAvailabilityDTO(roomName, bookings);
                })
                .collect(Collectors.toList());
    }

    // ── Helpers ──────────────────────────────────────────────

    private void mapDtoToEntity(SprintDTO dto, Sprint sprint) {
        sprint.setTitle(dto.getTitle());
        sprint.setTechnology(dto.getTechnology());
        sprint.setCohort(dto.getCohort());
        sprint.setStartDate(dto.getStartDate());
        sprint.setEndDate(dto.getEndDate());
        sprint.setSprintStart(dto.getSprintStart());
        sprint.setSprintEnd(dto.getSprintEnd());
        sprint.setRoom(dto.getRoom());
        sprint.setStatus(dto.getStatus());
        sprint.setInstructions(dto.getInstructions());

        if (dto.getTrainer() != null) {
            userRepository.findByName(dto.getTrainer()).ifPresent(sprint::setTrainer);
        }

        if (dto.getCohorts() != null) {
            try {
                sprint.setCohortsJson(objectMapper.writeValueAsString(dto.getCohorts()));
            } catch (Exception ignored) {}
        }
    }

    private SprintDTO toDTO(Sprint sprint) {
        SprintDTO dto = new SprintDTO();
        dto.setId(sprint.getId());
        dto.setTitle(sprint.getTitle());
        dto.setTechnology(sprint.getTechnology());
        dto.setCohort(sprint.getCohort());
        dto.setStartDate(sprint.getStartDate());
        dto.setEndDate(sprint.getEndDate());
        dto.setSprintStart(sprint.getSprintStart());
        dto.setSprintEnd(sprint.getSprintEnd());
        dto.setRoom(sprint.getRoom());
        dto.setStatus(sprint.getStatus());
        dto.setInstructions(sprint.getInstructions());
        
        if (sprint.getTrainer() != null) {
            dto.setTrainer(sprint.getTrainer().getName());
        }
        
        if (sprint.getCohortsJson() != null) {
            try {
                List<SprintDTO.CohortPair> pairs = objectMapper.readValue(
                        sprint.getCohortsJson(),
                        new com.fasterxml.jackson.core.type.TypeReference<List<SprintDTO.CohortPair>>() {});
                dto.setCohorts(pairs);
            } catch (Exception ignored) {}
        }
        
        return dto;
    }
}
