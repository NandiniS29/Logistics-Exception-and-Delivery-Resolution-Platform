package com.logistics.logisticsbackend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {
        "http://127.0.0.1:5500",
        "http://localhost:5500"
})
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(
            UserService userService,
            UserRepository userRepository) {

        this.userService = userService;
        this.userRepository = userRepository;
    }

    // =====================================================
    // CUSTOMER REGISTRATION
    // POST /api/users/register
    // =====================================================

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(
            @RequestBody User user) {

        try {

            User savedUser = userService.registerCustomer(user);

            Map<String, Object> response = new HashMap<>();

            response.put("message",
                    "Customer account created successfully.");

            response.put("userId", savedUser.getUserId());
            response.put("username", savedUser.getUsername());
            response.put("fullName", savedUser.getFullName());
            response.put("email", savedUser.getEmail());
            response.put("role", savedUser.getRole());
            response.put("status", savedUser.getStatus());

            // Never return the password in the response.
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException exception) {

            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            exception.getMessage()
                    ));

        } catch (org.springframework.dao.DataIntegrityViolationException exception) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            "An account with these details may already exist."
                    ));
        }
    }

    // =====================================================
    // GET ALL USERS
    // GET /api/users
    // =====================================================

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {

        return ResponseEntity.ok(
                userService.getAllUsers()
        );
    }

    // =====================================================
    // GET CURRENT LOGGED-IN USER
    // GET /api/users/me
    // =====================================================

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(
            Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication.getName().equals("anonymousUser")) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "User is not logged in."
                    ));
        }

        String username = authentication.getName();

        User user = userRepository
                .findByUsername(username)
                .orElse(null);

        if (user == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "User not found."
                    ));
        }

        Map<String, Object> response = new HashMap<>();

        response.put("userId", user.getUserId());
        response.put("username", user.getUsername());
        response.put("fullName", user.getFullName());
        response.put("role", user.getRole());
        response.put("status", user.getStatus());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("alternativePhone", user.getAlternativePhone());
        response.put("address", user.getAddress());
        response.put("city", user.getCity());
        response.put("state", user.getState());
        response.put("pincode", user.getPincode());

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // GET USER BY ID
    // GET /api/users/{id}
    // =====================================================

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Integer id) {

        return userService.getUserById(id)
                .map(user -> {

                    Map<String, Object> response = new HashMap<>();

                    response.put("userId", user.getUserId());
                    response.put("username", user.getUsername());
                    response.put("fullName", user.getFullName());
                    response.put("role", user.getRole());
                    response.put("status", user.getStatus());
                    response.put("email", user.getEmail());
                    response.put("phone", user.getPhone());

                    return ResponseEntity.ok(response);
                })
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // =====================================================
    // CREATE USER
    // POST /api/users
    // =====================================================

    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestBody User user) {

        User savedUser = userService.createUser(user);

        Map<String, Object> response = new HashMap<>();

        response.put("userId", savedUser.getUserId());
        response.put("username", savedUser.getUsername());
        response.put("fullName", savedUser.getFullName());
        response.put("role", savedUser.getRole());
        response.put("email", savedUser.getEmail());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // =====================================================
    // UPDATE USER
    // PUT /api/users/{id}
    // =====================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer id,
            @RequestBody User user) {

        User updatedUser = userService.updateUser(id, user);

        if (updatedUser == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        Map<String, Object> response = new HashMap<>();

        response.put("userId", updatedUser.getUserId());
        response.put("username", updatedUser.getUsername());
        response.put("fullName", updatedUser.getFullName());
        response.put("role", updatedUser.getRole());
        response.put("status", updatedUser.getStatus());
        response.put("email", updatedUser.getEmail());

        return ResponseEntity.ok(response);
    }

    // =====================================================
    // DELETE USER
    // DELETE /api/users/{id}
    // =====================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Integer id) {

        boolean deleted = userService.deleteUser(id);

        if (!deleted) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        return ResponseEntity.noContent().build();
    }
}