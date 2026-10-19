package com.logistics.logisticsbackend;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/consignees")
@CrossOrigin(origins = "*")
public class ConsigneeController {

    private final ConsigneeService consigneeService;

    public ConsigneeController(
            ConsigneeService consigneeService) {

        this.consigneeService = consigneeService;
    }

    @GetMapping
    public ResponseEntity<List<Consignee>> getAllConsignees() {

        return ResponseEntity.ok(
                consigneeService.getAllConsignees()
        );
    }

    @GetMapping("/{consigneeId}")
    public ResponseEntity<?> getConsigneeById(
            @PathVariable Integer consigneeId) {

        Consignee consignee =
                consigneeService
                        .getConsigneeById(consigneeId);

        if (consignee == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Consignee not found.");
        }

        return ResponseEntity.ok(consignee);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Consignee>>
    getConsigneesByCustomerId(
            @PathVariable Integer customerId) {

        return ResponseEntity.ok(
                consigneeService
                        .getConsigneesByCustomerId(customerId)
        );
    }

    @PostMapping
    public ResponseEntity<Consignee> createConsignee(
            @RequestBody Consignee consignee) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        consigneeService
                                .createConsignee(consignee)
                );
    }

    @PutMapping("/{consigneeId}")
    public ResponseEntity<?> updateConsignee(
            @PathVariable Integer consigneeId,
            @RequestBody Consignee consignee) {

        Consignee updated =
                consigneeService.updateConsignee(
                        consigneeId,
                        consignee
                );

        if (updated == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Consignee not found.");
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{consigneeId}")
    public ResponseEntity<?> deleteConsignee(
            @PathVariable Integer consigneeId) {

        boolean deleted =
                consigneeService
                        .deleteConsignee(consigneeId);

        if (!deleted) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Consignee not found.");
        }

        return ResponseEntity.ok(
                "Consignee deleted successfully."
        );
    }
}
