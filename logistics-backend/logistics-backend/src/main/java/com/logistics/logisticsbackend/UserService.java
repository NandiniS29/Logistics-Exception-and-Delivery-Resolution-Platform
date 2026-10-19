package com.logistics.logisticsbackend;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /*
     * Register a customer in both users and customers tables.
     */
    @Transactional
    public User registerCustomer(User user) {

        String email = user.getEmail() == null
                ? ""
                : user.getEmail().trim().toLowerCase();

        String phone = user.getPhone() == null
                ? ""
                : user.getPhone().trim();

        String fullName = user.getFullName() == null
                ? ""
                : user.getFullName().trim();

        String rawPassword = user.getPassword();

        if (fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Please enter your full name.");
        }

        if (email.isBlank()) {
            throw new IllegalArgumentException(
                    "Please enter your email address.");
        }

        if (phone.isBlank()) {
            throw new IllegalArgumentException(
                    "Please enter your phone number.");
        }

        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters.");
        }

        if (userRepository.findByEmail(email).isPresent()
                || userRepository.findByUsername(email).isPresent()) {
            throw new IllegalArgumentException(
                    "An account with this email already exists.");
        }

        user.setUsername(email);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("CUSTOMER");
        user.setStatus("ACTIVE");

        // Save the login account first.
        User savedUser = userRepository.saveAndFlush(user);

        // Create the corresponding customer profile if missing.
        Customer customer = customerRepository
                .findByEmail(email)
                .orElseGet(Customer::new);

        customer.setCustomerName(fullName);
        customer.setEmail(email);
        customer.setPhone(phone);
        customer.setCustomerStatus("ACTIVE");

        // Save the customer profile.
        customerRepository.saveAndFlush(customer);

        return savedUser;
    }

    /*
     * Existing user creation method.
     */
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Integer userId, User updatedUser) {

        Optional<User> existingUserOptional =
                userRepository.findById(userId);

        if (existingUserOptional.isEmpty()) {
            return null;
        }

        User existingUser = existingUserOptional.get();

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setRole(updatedUser.getRole());
        existingUser.setStatus(updatedUser.getStatus());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setAlternativePhone(
                updatedUser.getAlternativePhone());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setCity(updatedUser.getCity());
        existingUser.setState(updatedUser.getState());
        existingUser.setPincode(updatedUser.getPincode());

        if (updatedUser.getPassword() != null
                && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(
                    passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    public boolean deleteUser(Integer userId) {

        if (!userRepository.existsById(userId)) {
            return false;
        }

        userRepository.deleteById(userId);
        return true;
    }
}