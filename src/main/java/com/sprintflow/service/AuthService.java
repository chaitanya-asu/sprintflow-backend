package com.sprintflow.service;

import com.sprintflow.dto.LoginDTO;
import com.sprintflow.dto.AuthResponseDTO;
import com.sprintflow.entity.Role;
import com.sprintflow.entity.User;
import com.sprintflow.exception.AuthenticationException;
import com.sprintflow.exception.ResourceNotFoundException;
import com.sprintflow.repository.UserRepository;
import com.sprintflow.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtTokenProvider jwtTokenProvider;
    @Autowired private UserService      userService;

    public AuthResponseDTO login(LoginDTO loginDTO) {
        User user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid email or password"));

        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword()))
            throw new AuthenticationException("Invalid email or password");

        if (!"Active".equalsIgnoreCase(user.getStatus()))
            throw new AuthenticationException("User account is inactive");

        String accessToken  = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail(), user.getRole().name());

        AuthResponseDTO.UserInfo userInfo = new AuthResponseDTO.UserInfo(
                user.getId(), user.getName(), user.getEmail(), user.getRole().name()
        );
        userInfo.setStatus(user.getStatus());

        return new AuthResponseDTO(accessToken, refreshToken, userInfo, jwtTokenProvider.getExpirationTime() / 1000);
    }

    public AuthResponseDTO refresh(String refreshToken) {
        User user = validateToken(refreshToken);
        String newAccessToken = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        AuthResponseDTO.UserInfo userInfo = new AuthResponseDTO.UserInfo(
                user.getId(), user.getName(), user.getEmail(), user.getRole().name()
        );
        return new AuthResponseDTO(newAccessToken, refreshToken, userInfo, jwtTokenProvider.getExpirationTime() / 1000);
    }

    public User validateToken(String token) {
        if (!jwtTokenProvider.validateToken(token))
            throw new AuthenticationException("Invalid or expired token");

        String email = jwtTokenProvider.getEmailFromToken(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public AuthResponseDTO getCurrentUserInfo(String token) {
        User user = validateToken(token);
        AuthResponseDTO.UserInfo userInfo = new AuthResponseDTO.UserInfo(
                user.getId(), user.getName(), user.getEmail(), user.getRole().name()
        );
        return new AuthResponseDTO(token, null, userInfo, 0);
    }

    public com.sprintflow.dto.UserDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userService.toDTO(user);
    }

    public com.sprintflow.dto.UserDTO updateProfile(String email, com.sprintflow.dto.UserDTO dto) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userService.updateUser(user.getId(), dto);
    }

    public void changePassword(String email, String oldPassword, String newPassword) {
        if (oldPassword == null || newPassword == null || newPassword.length() < 6)
            throw new AuthenticationException("Invalid password");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new AuthenticationException("Current password is incorrect");
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordChanged(true);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        userRepository.save(user);
    }
}
