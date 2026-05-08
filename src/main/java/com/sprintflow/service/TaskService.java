package com.sprintflow.service;

import com.sprintflow.dto.TaskDTO;
import com.sprintflow.entity.Sprint;
import com.sprintflow.entity.Task;
import com.sprintflow.repository.SprintRepository;
import com.sprintflow.repository.TaskRepository;
import com.sprintflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final SprintRepository sprintRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getMyTasks() {
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail == null) {
            return List.of();
        }
        return taskRepository.findByAssignedTo(currentUserEmail).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksBySprint(Long sprintId) {
        return taskRepository.findBySprintId(sprintId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByAssignee(String email) {
        return taskRepository.findByAssignedTo(email).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getCreatedByMe() {
        String currentUserEmail = getCurrentUserEmail();
        if (currentUserEmail == null) {
            return List.of();
        }
        return taskRepository.findByCreatedBy(currentUserEmail).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskDTO createTask(TaskDTO dto) {
        Task task = new Task();
        mapDtoToEntity(dto, task);
        
        // Set creator if not provided
        if (task.getCreatedBy() == null) {
            task.setCreatedBy(getCurrentUserEmail());
        }
        
        return toDTO(taskRepository.save(task));
    }

    @Transactional
    public TaskDTO updateTask(Long id, TaskDTO dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));
        mapDtoToEntity(dto, task);
        return toDTO(taskRepository.save(task));
    }

    @Transactional
    public void updateStatus(Long id, String status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));
        try {
            task.setStatus(Task.TaskStatus.fromDisplayName(status));
            taskRepository.save(task);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status + ". Valid values: To Do, In Progress, Completed, Blocked");
        }
    }

    @Transactional
    public void reassignTask(Long id, String assignedTo) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found: " + id));
        task.setAssignedTo(assignedTo);
        taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    private void mapDtoToEntity(TaskDTO dto, Task task) {
        if (dto.getTitle() != null) {
            task.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            try {
                task.setStatus(Task.TaskStatus.fromDisplayName(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                // Keep existing status if invalid
            }
        }
        if (dto.getPriority() != null) {
            try {
                task.setPriority(Task.TaskPriority.fromDisplayName(dto.getPriority()));
            } catch (IllegalArgumentException e) {
                // Keep existing priority if invalid
            }
        }
        if (dto.getAssignedTo() != null) {
            task.setAssignedTo(dto.getAssignedTo());
        }
        if (dto.getCreatedBy() != null) {
            task.setCreatedBy(dto.getCreatedBy());
        }
        if (dto.getDueDate() != null) {
            task.setDueDate(dto.getDueDate().atStartOfDay());
        }

        if (dto.getSprintId() != null) {
            Sprint sprint = sprintRepository.findById(dto.getSprintId()).orElse(null);
            task.setSprint(sprint);
        }
    }

    private TaskDTO toDTO(Task task) {
        if (task == null) return null;
        
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus() != null ? task.getStatus().getDisplayName() : null);
        dto.setPriority(task.getPriority() != null ? task.getPriority().getDisplayName() : null);
        dto.setAssignedTo(task.getAssignedTo());
        dto.setCreatedBy(task.getCreatedBy());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        
        if (task.getDueDate() != null) {
            dto.setDueDate(task.getDueDate().toLocalDate());
        }
        
        if (task.getSprint() != null) {
            dto.setSprintId(task.getSprint().getId());
            dto.setSprintTitle(task.getSprint().getTitle());
        }
        
        return dto;
    }
}