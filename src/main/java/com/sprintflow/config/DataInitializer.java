package com.sprintflow.config;

import com.sprintflow.entity.User;
import com.sprintflow.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private static final String DEFAULT_PASSWORD = "Admin@123";
    private static volatile boolean initialized = false;

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void resetPasswords() {
        if (initialized) {
            logger.debug("DataInitializer already executed, skipping");
            return;
        }

        try {
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                logger.info("No users found in database");
                return;
            }

            String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);
            List<User> usersNeedingReset = users.stream()
                    .filter(u -> !passwordEncoder.matches(DEFAULT_PASSWORD, u.getPassword()))
                    .collect(Collectors.toList());

            if (!usersNeedingReset.isEmpty()) {
                usersNeedingReset.forEach(u -> u.setPassword(encodedPassword));
                userRepository.saveAll(usersNeedingReset);
                logger.info("[DataInitializer] Reset {} user passwords to default", usersNeedingReset.size());
            } else {
                logger.debug("All user passwords are already set to default");
            }

            initialized = true;
        } catch (Exception e) {
            logger.error("Error during DataInitializer execution", e);
            throw new RuntimeException("Failed to initialize data", e);
        }
    }
}
