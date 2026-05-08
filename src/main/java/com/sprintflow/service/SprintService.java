package com.sprintflow.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprintflow.dto.RoomAvailabilityDTO;
import com.sprintflow.dto.SprintDTO;
import com.sprintflow.entity.Employee;
import com.sprintflow.entity.Sprint;
import com.sprintflow.entity.SprintEmployee;
import com.sprintflow.entity.User;
import com.sprintflow.exception.ResourceNotFoundException;
import com.sprintflow.repository.EmployeeRepository;
import com.sprintflow.repository.SprintEmployeeRepository;
import com.sprintflow.repository.SprintRepository;
import com.sprintflow.repository.UserRepository;

@Service
public class SprintService {

    private static final Logger logger = LoggerFactory.getLogger(SprintService.class);

    @Autowired private SprintRepository sprintRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private SprintEmployeeRepository sprintEmployeeRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private EmployeeService employeeService;
    @Autowired private AuditLogService auditLogService;
    @Autowired private WebSocketSyncService webSocketSyncService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public List<SprintDTO> getAllSprints() {
        return sprintRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SprintDTO> getSprintsByStatus(String status) {
        return sprintRepository.findByStatus(status).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SprintDTO getSprintById(Long id) {
        return toDTO(sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + id)));
    }

    @Transactional(readOnly = true)
    public List<SprintDTO> getSprintsForTrainer(Long trainerId) {
        return sprintRepository.findByTrainerId(trainerId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<SprintDTO> getSprintsByFilters(String status, String technology, String keyword, Pageable pageable) {
        return sprintRepository.findByFilters(status, technology, keyword, pageable).map(this::toDTO);
    }

    public List<SprintDTO> filterSprintsByKeyword(List<SprintDTO> sprints, String keyword) {
        return sprints.stream()
                .filter(s -> (s.getTitle()   != null && s.getTitle().toLowerCase().contains(keyword))
                          || (s.getTrainer() != null && s.getTrainer().toLowerCase().contains(keyword)))
                .collect(Collectors.toList());
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
        
        // Broadcast sprint creation
        try {
            webSocketSyncService.broadcastSprintUpdate(saved.getId(), "CREATE", toDTO(saved));
        } catch (Exception e) {
            logger.error("Error broadcasting sprint creation", e);
        }
        
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
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
                notificationService.create(notification);
            } catch (Exception e) {
                logger.error("Error creating notification for trainer: {}", saved.getTrainer().getEmail(), e);
            }
        }

        // Audit Log
        try {
            auditLogService.log("CREATE_SPRINT", createdBy != null ? createdBy.toString() : "SYSTEM", "Created sprint: " + saved.getTitle(), "Sprint", saved.getId());
        } catch (Exception e) {
            logger.error("Error logging audit action", e);
        }

        return toDTO(saved);
    }

    @Transactional
    public SprintDTO updateSprint(Long id, SprintDTO dto) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + id));
        
        // Validate required fields before update
        if (dto.getStartDate() == null && sprint.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        if (dto.getEndDate() == null && sprint.getEndDate() == null) {
            throw new IllegalArgumentException("End date is required");
        }
        
        mapDtoToEntity(dto, sprint);
        sprint.setUpdatedAt(LocalDateTime.now());
        Sprint updated = sprintRepository.save(sprint);
        
        // Broadcast sprint update
        try {
            webSocketSyncService.broadcastSprintUpdate(id, "UPDATE", toDTO(updated));
        } catch (Exception e) {
            logger.error("Error broadcasting sprint update", e);
        }

        if (updated.getTrainer() != null) {
            try {
                com.sprintflow.entity.Notification notification = com.sprintflow.entity.Notification.builder()
                    .userEmail(updated.getTrainer().getEmail())
                    .title("Sprint Updated")
                    .message("The sprint '" + updated.getTitle() + "' has been updated.")
                    .type("INFO")
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
                notificationService.create(notification);
            } catch (Exception e) {
                logger.error("Error creating notification for trainer: {}", updated.getTrainer().getEmail(), e);
            }
        }
        
        return toDTO(updated);
    }

    @Transactional
    public SprintDTO updateStatus(Long id, String status) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + id));
        sprint.setStatus(status);
        sprint.setUpdatedAt(LocalDateTime.now());
        Sprint updated = sprintRepository.save(sprint);
        
        // Broadcast status change
        try {
            webSocketSyncService.broadcastSprintUpdate(id, "STATUS_CHANGE", toDTO(updated));
        } catch (Exception e) {
            logger.error("Error broadcasting sprint status change", e);
        }
        
        return toDTO(updated);
    }

    @Transactional
    public void deleteSprint(Long id) {
        Sprint sprint = sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint not found: " + id));
        
        String title = sprint.getTitle();
        String trainerEmail = sprint.getTrainer() != null ? sprint.getTrainer().getEmail() : null;

        sprintRepository.deleteById(id);
        
        // Broadcast sprint deletion
        try {
            webSocketSyncService.broadcastSprintUpdate(id, "DELETE", null);
        } catch (Exception e) {
            logger.error("Error broadcasting sprint deletion", e);
        }

        if (trainerEmail != null) {
            try {
                com.sprintflow.entity.Notification notification = com.sprintflow.entity.Notification.builder()
                    .userEmail(trainerEmail)
                    .title("Sprint Cancelled")
                    .message("The sprint '" + title + "' has been cancelled/deleted.")
                    .type("WARNING")
                    .isRead(false)
                    .createdAt(LocalDateTime.now())
                    .build();
                notificationService.create(notification);
            } catch (Exception e) {
                logger.error("Error creating notification for trainer: {}", trainerEmail, e);
            }
        }
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

    // â”€â”€ Helpers â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void mapDtoToEntity(SprintDTO dto, Sprint sprint) {
        if (dto == null) {
            throw new IllegalArgumentException("Sprint DTO cannot be null");
        }

        if (dto.getTitle() != null) sprint.setTitle(dto.getTitle());
        if (dto.getTechnology() != null) sprint.setTechnology(dto.getTechnology());
        if (dto.getCohort() != null) sprint.setCohort(dto.getCohort());
        if (dto.getStartDate() != null) sprint.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) sprint.setEndDate(dto.getEndDate());
        if (dto.getSprintStart() != null) sprint.setSprintStart(dto.getSprintStart());
        if (dto.getSprintEnd() != null) sprint.setSprintEnd(dto.getSprintEnd());
        if (dto.getRoom() != null) sprint.setRoom(dto.getRoom());
        if (dto.getStatus() != null) sprint.setStatus(dto.getStatus());
        if (dto.getInstructions() != null) sprint.setInstructions(dto.getInstructions());

        if (dto.getTrainerId() != null) {
            userRepository.findById(dto.getTrainerId()).ifPresentOrElse(
                sprint::setTrainer,
                () -> logger.warn("Trainer not found by ID: {}", dto.getTrainerId())
            );
        } else if (dto.getTrainer() != null && !dto.getTrainer().isBlank()) {
            userRepository.findByName(dto.getTrainer()).ifPresentOrElse(
                sprint::setTrainer,
                () -> logger.warn("Trainer not found by name: {}", dto.getTrainer())
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

    @Transactional
    public void blockEmployeeFromSprint(Long sprintId, Long employeeId, String reason, Long blockedByUserId) {
        SprintEmployee enrollment = sprintEmployeeRepository.findBySprintIdAndEmployeeId(sprintId, employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not enrolled in this sprint"));
        User blocker = userRepository.findById(blockedByUserId).orElse(null);
        enrollment.setBlocked(true);
        enrollment.setBlockedReason(reason);
        enrollment.setBlockedBy(blocker);
        sprintEmployeeRepository.save(enrollment);
        logger.info("Employee {} blocked from sprint {} by user {}", employeeId, sprintId, blockedByUserId);
    }

    @Transactional
    public void unblockEmployeeFromSprint(Long sprintId, Long employeeId) {
        SprintEmployee enrollment = sprintEmployeeRepository.findBySprintIdAndEmployeeId(sprintId, employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not enrolled in this sprint"));
        enrollment.setBlocked(false);
        enrollment.setBlockedReason(null);
        enrollment.setBlockedBy(null);
        sprintEmployeeRepository.save(enrollment);
        logger.info("Employee {} unblocked from sprint {}", employeeId, sprintId);
    }

    @Transactional(readOnly = true)
    public List<SprintEmployee> getBlockedEmployeesForSprint(Long sprintId) {
        return sprintEmployeeRepository.findBySprintIdAndBlockedTrue(sprintId);
    }

    @Transactional(readOnly = true)
    public boolean isEmployeeBlockedFromSprint(Long sprintId, Long employeeId) {
        return sprintEmployeeRepository.findBySprintIdAndEmployeeId(sprintId, employeeId)
                .map(SprintEmployee::isBlocked)
                .orElse(false);
    }

    private SprintDTO toDTO(Sprint sprint) {
        if (sprint == null) {
            return null;
        }

        SprintDTO dto = new SprintDTO();
        dto.setId(sprint.getId());
        dto.setTitle(sprint.getTitle());
        dto.setSprintType(sprint.getSprintType());
        dto.setTechnology(sprint.getTechnology());
        dto.setSprintSubject(sprint.getSprintSubject());
        dto.setSprintCommunicationType(sprint.getSprintCommunicationType());
        dto.setCohort(sprint.getCohort());
        dto.setStartDate(sprint.getStartDate());
        dto.setEndDate(sprint.getEndDate());
        dto.setSprintStart(sprint.getSprintStart());
        dto.setSprintEnd(sprint.getSprintEnd());
        dto.setRoom(sprint.getRoom());
        dto.setStatus(sprint.getStatus());
        dto.setInstructions(sprint.getInstructions());
        dto.setCreatedAt(sprint.getCreatedAt());
        dto.setUpdatedAt(sprint.getUpdatedAt());
        
        if (sprint.getTrainer() != null) {
            dto.setTrainer(sprint.getTrainer().getName());
            dto.setTrainerId(sprint.getTrainer().getId());
        }
        
        // Set employee count from enrollments
        if (sprint.getEnrollments() != null) {
            dto.setEmployeeCount(sprint.getEnrollments().size());
        } else {
            dto.setEmployeeCount(0);
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

