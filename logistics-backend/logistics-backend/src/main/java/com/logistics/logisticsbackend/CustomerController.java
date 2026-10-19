package com.logistics.logisticsbackend;

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
@RequestMapping("/api/customers")
@CrossOrigin(origins = {
    "http://127.0.0.1:5500",
    "http://localhost:5500"
})
public class CustomerController {

    private final CustomerService customerService;
    private final UserRepository userRepository;

    public CustomerController(
            CustomerService customerService,
            UserRepository userRepository) {

        this.customerService = customerService;
        this.userRepository = userRepository;
    }

    // =========================================================
    // GET ALL CUSTOMERS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(
                customerService.getAllCustomers()
        );
    }

    // =========================================================
    // GET CURRENT LOGGED-IN CUSTOMER
    // =========================================================

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(
            Authentication authentication) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please log in first.");
        }

        try {
            User user = userRepository
                    .findByUsername(authentication.getName())
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Logged-in user not found.");
            }

            Customer customer = customerService
                    .getCustomerByEmail(user.getEmail());

            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Customer profile not found for email: "
                                + user.getEmail());
            }

            return ResponseEntity.ok(customer);

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to load customer profile.");
        }
    }

    // =========================================================
    // UPDATE CURRENT LOGGED-IN CUSTOMER
    // =========================================================

    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            Authentication authentication,
            @RequestBody Customer customer) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please log in first.");
        }

        try {
            User user = userRepository
                    .findByUsername(authentication.getName())
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Logged-in user not found.");
            }

            Customer existingCustomer = customerService
                    .getCustomerByEmail(user.getEmail());

            if (existingCustomer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Customer profile not found for email: "
                                + user.getEmail());
            }

            Customer updated = customerService.updateCustomer(
                    existingCustomer.getCustomerId(),
                    customer
            );

            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unable to update customer profile.");
        }
    }

    // =========================================================
    // GET CUSTOMER BY ID
    // =========================================================

    @GetMapping("/{customerId:\\d+}")
    public ResponseEntity<?> getCustomerById(
            @PathVariable Integer customerId) {

        Customer customer =
                customerService.getCustomerById(customerId);

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer not found.");
        }

        return ResponseEntity.ok(customer);
    }

    // =========================================================
    // GET CUSTOMER BY EMAIL
    // =========================================================

    @GetMapping("/email/{email}")
    public ResponseEntity<?> getCustomerByEmail(
            @PathVariable String email) {

        Customer customer =
                customerService.getCustomerByEmail(email);

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer not found.");
        }

        return ResponseEntity.ok(customer);
    }

    // =========================================================
    // CREATE CUSTOMER
    // Accepts fullName, customerName, name, or customer_name
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createCustomer(
            @RequestBody Map<String, Object> request) {

        try {
            Object name = request.get("fullName");

            if (isBlank(name)) {
                name = request.get("customerName");
            }

            if (isBlank(name)) {
                name = request.get("name");
            }

            if (isBlank(name)) {
                name = request.get("customer_name");
            }

            if (isBlank(name)) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Customer name is required. Received fields: "
                                        + request.keySet()
                        ));
            }

            Object email = request.get("email");

            if (isBlank(email)) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "message",
                                "Customer email is required."
                        ));
            }

            Customer customer = new Customer();

            customer.setCustomerName(name.toString().trim());
            customer.setEmail(email.toString().trim());

            Object phone = request.get("phone");
            if (phone != null) {
                customer.setPhone(phone.toString().trim());
            }

            Object city = request.get("city");
            if (city != null) {
                customer.setCity(city.toString().trim());
            }

            Object address = request.get("address");
            if (address != null) {
                customer.setAddress(address.toString().trim());
            }

            Customer savedCustomer =
                    customerService.createCustomer(customer);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedCustomer);

        } catch (Exception e) {
            e.printStackTrace();

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "message",
                            "Unable to save customer. Check the backend terminal for details."
                    ));
        }
    }

    // Helper to validate empty or missing values
    private boolean isBlank(Object value) {
        return value == null || value.toString().isBlank();
    }

    // =========================================================
    // UPDATE CUSTOMER BY ID
    // =========================================================

    @PutMapping("/{customerId:\\d+}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Integer customerId,
            @RequestBody Customer customer) {

        Customer updated = customerService.updateCustomer(
                customerId,
                customer
        );

        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer not found.");
        }

        return ResponseEntity.ok(updated);
    }

    // =========================================================
    // DELETE CUSTOMER
    // =========================================================

    @DeleteMapping("/{customerId:\\d+}")
    public ResponseEntity<?> deleteCustomer(
            @PathVariable Integer customerId) {

        boolean deleted =
                customerService.deleteCustomer(customerId);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Customer not found.");
        }

        return ResponseEntity.ok(
                "Customer deleted successfully."
        );
    }
}
