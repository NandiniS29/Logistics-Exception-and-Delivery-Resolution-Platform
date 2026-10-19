package com.logistics.logisticsbackend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordInitializer {

    @Bean
    CommandLineRunner initializePasswords(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String controlPassword =
                    System.getenv("CONTROL_OFFICER_INITIAL_PASSWORD");

            if (controlPassword != null && !controlPassword.isBlank()) {
                userRepository.findByUsername("control.officer")
                        .ifPresent(user -> {
                            if (user.getPassword() == null
                                    || !user.getPassword().startsWith("$2a$")) {
                                user.setPassword(
                                        passwordEncoder.encode(controlPassword)
                                );
                                userRepository.save(user);
                            }
                        });
            }

            String customerPassword =
                    System.getenv("CUSTOMER_INITIAL_PASSWORD");

            if (customerPassword != null && !customerPassword.isBlank()) {
                userRepository.findByUsername("customer.one")
                        .ifPresent(user -> {
                            if (user.getPassword() == null
                                    || !user.getPassword().startsWith("$2a$")) {
                                user.setPassword(
                                        passwordEncoder.encode(customerPassword)
                                );
                                userRepository.save(user);
                            }
                        });
            }
        };
    }
}