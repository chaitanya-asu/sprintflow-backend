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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SprintService {

    private static final Logger logger = LoggerFactory.getLogger(SprintService.class);

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
        if (dto == null) {
            throw new IllegalArgumentException("Sprint DTO cannot be null");
        }

        Sprint sprint = new Sprint();
        mapDtoToEntity(dto, sprint);
        
        User creator = null;
        if (createdBy != null) {
            creator = userRepository.findById(createdBy).orElse(null);
            if (creator == null) {
                logger.warn("Creator user not found: {}", createdBy);
            }
        }
        sprint.setCreatedBy(creator);
        sprint.setCreatedAt(LocalDateTime.now());
        sprint.setUpdatedAt(LocalDateTime.now());

        Sprint saved = sprintRepository.save(sprint);
        logger.info("Sprint created successfully: {} (ID: {})", saved.getTitle(), saved.getId());
        
        try {
            employeeService.autoEnrollByCohorts(saved.getId());
        } catch (Exception e) {
            logger.error("Error auto-enrolling employees for sprint: {}", saved.getId(), e);
        }

        if (saved.getTrainer() != null) {
            try {
                com.sprintflow.entity.Notification notification = com.sprintflow.entity.Notification.builder()
                    .userEmail(saved.getTrainer().getEmail())
                    .title("New Sprint Assigned")
                    .message("You have been assigned as a trainer for: " + saved.getTitle())
                    .type("INFO")
                    .read(false)
                    .createdAt(LocalDateTime.now())
                    .build();
                notificationService.create(notification);
            } catch (Exception e) {
                logger.error("Error creating notification for trainer: {}", saved.getTrainer().getEmail(), e);
            }
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
        if (dto == null) {
            throw new IllegalArgumentException("Sprint DTO cannot be null");
        }

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

        if (dto.getTrainer() != null && !dto.getTrainer().isBlank()) {
            userRepository.findByName(dto.getTrainer()).ifPresentOrElse(
                sprint::setTrainer,
                () -> logger.warn("Trainer not found: {}", dto.getTrainer())
            );
        }

        if (dto.getCohorts() != null && !dto.getCohorts().isEmpty()) {
            try {
                sprint.setCohortsJson(objectMapper.writeValueAsString(dto.getCohorts()));
            } catch (Exception e) {
                logger.error("Error serializing cohorts JSON", e);
            }
        }
    }

    private SprintDTO toDTO(Sprint sprint) {
        if (sprint == null) {
            return null;
        }

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
        
        if (sprint.getCohortsJson() != null && !sprint.getCohortsJson().isBlank()) {
            try {
                List<SprintDTO.CohortPair> pairs = objectMapper.readValue(
                        sprint.getCohortsJson(),
                        new com.fasterxml.jackson.core.type.TypeReference<List<SprintDTO.CohortPair>>() {});
                dto.setCohorts(pairs);
            } catch (Exception e) {
                logger.error("Error deserializing cohorts JSON for sprint: {}", sprint.getId(), e);
            }
        }
        
        return dto;
    }
}
