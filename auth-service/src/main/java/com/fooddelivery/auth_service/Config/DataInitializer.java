package com.fooddelivery.auth_service.Config;

import com.fooddelivery.auth_service.Entity.User;
import com.fooddelivery.auth_service.Enums.Role;
import com.fooddelivery.auth_service.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@fooddelivery.com")) {
            User superAdmin = User.builder()
                    .email("admin@fooddelivery.com")
                    .passwordHash(passwordEncoder.encode("Admin123!"))
                    .role(Role.SUPER_ADMIN)
                    .isActive(true)
                    .forcePasswordChange(false)
                    .build();
            userRepository.save(superAdmin);
            System.out.println("Суперадмин создан: admin@fooddelivery.com");
        }
    }
}