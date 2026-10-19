package com.logistics.logisticsbackend;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Find account using username
    Optional<User> findByUsername(String username);

    // Find account using email
    Optional<User> findByEmail(String email);

    // Find account using email, ignoring uppercase/lowercase differences
    Optional<User> findByEmailIgnoreCase(String email);

    // Find account using full name, ignoring uppercase/lowercase differences
    Optional<User> findByFullNameIgnoreCase(String fullName);
}