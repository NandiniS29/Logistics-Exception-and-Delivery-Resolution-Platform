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
@RequestMapping("/api/shipments")
@CrossOrigin(
    origins = "http://127.0.0.1:5500",
    allowCredentials = "true"
)
public class ShipmentController {

    private final ShipmentService shipmentService;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    public ShipmentController(
            ShipmentService shipmentService,
            UserRepository userRepository,
            CustomerRepository customerRepository) {

        this.shipmentService = shipmentService;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    // GET ALL SHIPMENTS
    @GetMapping
    public ResponseEntity<?> getAllShipments() {

        return ResponseEntity.ok(
                shipmentService.getAllShipments()
        );
    }

    // GET CURRENT CUSTOMER'S SHIPMENTS
    @GetMapping("/my")
    public ResponseEntity<?> getMyShipments(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "message",
                            "User is not logged in."
                    ));
        }

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElse(null);

        if (user == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "User not found."
                    ));
        }

        if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {

            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message",
                            "This endpoint is only for customers."
                    ));
        }

        if (user.getEmail() == null
                || user.getEmail().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "Customer email is missing from the user account."
                    ));
        }

        Customer customer = customerRepository
                .findByEmail(user.getEmail())
                .orElse(null);

        if (customer == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "Customer profile not found for this account."
                    ));
        }

        List<Shipment> shipments =
                shipmentService.getShipmentsByCustomerId(
                        customer.getCustomerId()
                );

        return ResponseEntity.ok(shipments);
    }

    // GET SHIPMENT BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getShipmentById(
            @PathVariable Integer id) {

        Shipment shipment =
                shipmentService.getShipmentById(id);

        if (shipment == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "Shipment not found."
                    ));
        }

        return ResponseEntity.ok(shipment);
    }

    // GET SHIPMENT BY TRACKING NUMBER
    @GetMapping("/tracking/{trackingNumber}")
    public ResponseEntity<?> getShipmentByTrackingNumber(
            @PathVariable String trackingNumber) {

        Shipment shipment =
                shipmentService.getShipmentByTrackingNumber(
                        trackingNumber
                );

        if (shipment == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "Shipment not found."
                    ));
        }

        return ResponseEntity.ok(shipment);
    }

    // CREATE SHIPMENT
    @PostMapping
    public ResponseEntity<?> createShipment(
            @RequestBody Shipment shipment) {

        Shipment savedShipment =
                shipmentService.createShipment(shipment);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedShipment);
    }

    // UPDATE SHIPMENT
    @PutMapping("/{id}")
    public ResponseEntity<?> updateShipment(
            @PathVariable Integer id,
            @RequestBody Shipment shipment) {

        Shipment updatedShipment =
                shipmentService.updateShipment(id, shipment);

        if (updatedShipment == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "Shipment not found."
                    ));
        }

        return ResponseEntity.ok(updatedShipment);
    }

    // DELETE SHIPMENT
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShipment(
            @PathVariable Integer id) {

        boolean deleted =
                shipmentService.deleteShipment(id);

        if (!deleted) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "Shipment not found."
                    ));
        }

        return ResponseEntity.noContent().build();
    }
}
