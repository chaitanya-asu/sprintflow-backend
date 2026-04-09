package com.sprintflow.service;

import com.sprintflow.dto.UserDTO;
import com.sprintflow.entity.Role;
import com.sprintflow.entity.User;
import com.sprintflow.exception.DuplicateResourceException;
import com.sprintflow.exception.ResourceNotFoundException;
import com.sprintflow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired(required = false) private EmailService emailService;

    public UserDTO getUserById(Long id) {
        return toDTO(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id)));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<UserDTO> getUsersByRole(String roleStr) {
        Role role = Role.valueOf(roleStr.toUpperCase());
        return userRepository.findByRole(role).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // Manager creates trainer or HR — auto-generates password and emails credentials
    public UserDTO createUser(UserDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new DuplicateResourceException("Email already exists: " + dto.getEmail());

        String tempPassword = generatePassword();

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
        user.setPhone(dto.getPhone());
        user.setDepartment(dto.getDepartment());
        user.setTrainerRole(dto.getTrainerRole());
        user.setStatus("Active");
        user.setJoinedDate(dto.getJoinedDate());
        user.setTempPassword(tempPassword);
        user.setPasswordChanged(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        // Send credentials email
        if (emailService != null) {
            emailService.sendCredentials(saved.getEmail(), saved.getName(), tempPassword);
        }

        return toDTO(saved);
    }

    public UserDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        if (dto.getName()        != null) user.setName(dto.getName());
        if (dto.getPhone()       != null) user.setPhone(dto.getPhone());
        if (dto.getDepartment()  != null) user.setDepartment(dto.getDepartment());
        if (dto.getTrainerRole() != null) user.setTrainerRole(dto.getTrainerRole());
        if (dto.getStatus()      != null) user.setStatus(dto.getStatus());
        if (dto.getJoinedDate()  != null) user.setJoinedDate(dto.getJoinedDate());
        // Allow email update (manager changing their own email or updating a user's email)
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (!dto.getEmail().equalsIgnoreCase(user.getEmail()) &&
                    userRepository.existsByEmail(dto.getEmail())) {
                throw new com.sprintflow.exception.DuplicateResourceException(
                        "Email already in use: " + dto.getEmail());
            }
            user.setEmail(dto.getEmail().toLowerCase().trim());
        }
        user.setUpdatedAt(LocalDateTime.now());

        return toDTO(userRepository.save(user));
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        user.setStatus("Inactive");
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public void resendCredentials(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        String newTemp = generatePassword();
        user.setPassword(passwordEncoder.encode(newTemp));
        user.setTempPassword(newTemp);
        user.setPasswordChanged(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        if (emailService != null) emailService.sendCredentials(user.getEmail(), user.getName(), newTemp);
    }

    // ── Helpers ──────────────────────────────────────────────

    private String generatePassword() {
        // 8 chars alphanumeric + special suffix
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8) + "@Sf1";
    }

    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name().toLowerCase()); // lowercase for frontend
        dto.setPhone(user.getPhone());
        dto.setDepartment(user.getDepartment());
        dto.setTrainerRole(user.getTrainerRole());
        dto.setStatus(user.getStatus());
        dto.setJoinedDate(user.getJoinedDate());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
