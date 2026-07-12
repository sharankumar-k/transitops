package com.transitops.backend.config;

import com.transitops.backend.entity.User;
import com.transitops.backend.enums.Role;
import com.transitops.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            createUser(
                    userRepository,
                    passwordEncoder,
                    "fleet@transitops.com",
                    "Fleet@123",
                    Role.FLEET_MANAGER);

            createUser(
                    userRepository,
                    passwordEncoder,
                    "driver@transitops.com",
                    "Driver@123",
                    Role.DRIVER);

            createUser(
                    userRepository,
                    passwordEncoder,
                    "safety@transitops.com",
                    "Safety@123",
                    Role.SAFETY_OFFICER);

            createUser(
                    userRepository,
                    passwordEncoder,
                    "finance@transitops.com",
                    "Finance@123",
                    Role.FINANCIAL_ANALYST);
        };
    }

    private void createUser(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String email,
            String password,
            Role role) {

        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            userRepository.save(user);
        }
    }
}